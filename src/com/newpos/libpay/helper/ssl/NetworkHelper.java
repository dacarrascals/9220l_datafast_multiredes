package com.newpos.libpay.helper.ssl;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.newpos.pay.R;
import com.newpos.libpay.Logger;
import com.newpos.libpay.utils.ISOUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import static com.android.newpos.pay.StartAppDATAFAST.host_confi;
import static com.android.newpos.pay.StartAppDATAFAST.tablaIp;


//import static com.datafast.menus.menus.acquirerRow;

/**
 * 网络助手类
 * @author zhouqiang
 */
public class NetworkHelper {

	private Socket socket;//SSLSocket对象
	private InputStream is; // 输入流
	private OutputStream os; // 输出流
	private String ip;//连接IP地址
	private int port;//连接端口号
	private Context tcontext ;//上下文对象
	private int timeoutRsp; //超时时间
	private int timeoutCon;
	private int protocol; // 协议 0: 2字节长度+数据 1:stx协议
	private final String CLIENT_KEY_MANAGER = "X509"; // 密钥管理器
	private final String CLIENT_AGREEMENT = "TLSv1.2"; // 使用协议
	private final String CLIENT_KEY_KEYSTORE = "BKS"; // "JKS";//密库，这里用的是BouncyCastle密库
	private final String CLIENT_KEY_PASS = "123456";// 密码

	/**
	 * @param ip 初始化连接的IP
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public NetworkHelper(String ip, int port, int timeoutRes, int timeoutCone, Context context) {
		this.ip = ip;
		this.port = port;
		this.timeoutRsp = timeoutRes;
		this.timeoutCon = timeoutCone;
		this.tcontext = context;
	}

	/**
	 * 连接socket
	 * @return
	 * @throws IOException
	 */
	public int Connect() {
		try {

			if (ISOUtil.stringToBoolean(tablaIp.getTLS())){
				Resources res = tcontext.getResources();
				InputStream tlsKeyStore = res.openRawResource(R.raw.tool);
				AndroidSocketFactory sf = new AndroidSocketFactory(tlsKeyStore);
				sf.setAlgorithm("TLSv1.2");
				sf.setKeyPassword("123456");
				sf.setPassword("123456");
				sf.setServerAuthNeeded(false);
				sf.setClientAuthNeeded(false);
				socket = sf.createSocket(ip, port);

				socket.setSoTimeout(timeoutCon);

				is = socket.getInputStream();
				os = socket.getOutputStream();
			}else {
				socket = new Socket();
				socket.setSoTimeout(timeoutRsp);
				socket.connect(new InetSocketAddress(ip, port), timeoutCon);
				is = socket.getInputStream();
				os = socket.getOutputStream();
			}
		} catch (Exception e) {
			Logger.error("Exception" + e.toString());
			return -1;
		}
		return 0;
	}

	/**
	 * 关闭socket
	 */
	public int close() {
		try {
			socket.close();
		} catch (IOException e) {
			return -1;
		}
		return 0;
	}

	/**
	 * 发送数据包
	 * @param data
	 * @return
	 */
	public int Send(byte[] data) {
		byte[] newData = null;
		if (protocol == 0) {
			newData = new byte[data.length + 2];
			newData[0] = (byte) (data.length >> 8);
			newData[1] = (byte) data.length;// 丢失高位
			System.arraycopy(data, 0, newData, 2, data.length);
		}
		try {
			os.write(newData);
			os.flush();
		} catch (IOException e) {
			return -1;
		}
		return 0;
	}

	/**
	 * 接受数据包
	 * @return
	 * @throws IOException
	 */
	public byte[] Recive(int max) throws IOException {
		ByteArrayOutputStream byteOs ;
		byte[] resP = null ;
		if (protocol == 0) {
			byte[] packLen = new byte[2];
			int len ;
			byte[] bb = new byte[2+max];
			int i ;
			byteOs = new ByteArrayOutputStream();
			try {
				if ((i = is.read(packLen)) != -1) {
					len = ISOUtil.byte2int(packLen);
					while (len > 0 && (i = is.read(bb)) != -1) {
						byteOs.write(bb, 0, i);
						len -= i;
					}
				}
			} catch (InterruptedIOException e) {
				// 读取超时处理
				Log.w("PAY_SDK" , "Recive：读取流数据超时异常");
				return null;
			}
			resP = byteOs.toByteArray();
		}
		return resP;
	}

	public SSLSocket getSSLSocket() throws KeyManagementException,NoSuchAlgorithmException,
            KeyStoreException, CertificateException,IOException, UnrecoverableKeyException {
		SSLContext ctx = SSLContext.getInstance(CLIENT_AGREEMENT);
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(CLIENT_KEY_MANAGER);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(CLIENT_KEY_MANAGER);
		KeyStore ks = KeyStore.getInstance(CLIENT_KEY_KEYSTORE);
		KeyStore tks = KeyStore.getInstance(CLIENT_KEY_KEYSTORE);
		ks.load(tcontext.getAssets().open("client.bks"), CLIENT_KEY_PASS.toCharArray());
		tks.load(tcontext.getAssets().open("root.bks"), CLIENT_KEY_PASS.toCharArray());
		kmf.init(ks, CLIENT_KEY_PASS.toCharArray());
		tmf.init(tks);
		ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		return (SSLSocket) ctx.getSocketFactory().createSocket(ip, port);
	}

	public void checkConnection(){
		final String DEBUG_TAG = "NetworkStatusExample";
		ConnectivityManager connMgr = (ConnectivityManager) tcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isWifiConn = false;
		boolean isMobileConn = false;
		for (Network network : connMgr.getAllNetworks()) {
			NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				isWifiConn |= networkInfo.isConnected();
			}
			if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				isMobileConn |= networkInfo.isConnected();
			}
		}
		Log.d(DEBUG_TAG, "Wifi connected: " + isWifiConn);
		Log.d(DEBUG_TAG, "Mobile connected: " + isMobileConn);

	}
}
