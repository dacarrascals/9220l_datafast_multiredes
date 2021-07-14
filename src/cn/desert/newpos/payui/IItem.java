package cn.desert.newpos.payui;

import com.android.newpos.pay.R;

/**
 * @author zhouqiang
 * @email wy1376359644@163.com
 */

public interface IItem {
    /**
     * HOME布局
     */
    public interface Home{
        public static final int[] imgs = {
                /*R.drawable.menu_void,
                R.drawable.menu_query,
                R.drawable.menu_preauth,
                R.drawable.menu_manage,
                R.drawable.menu_appstore,
                R.drawable.menu_program_setting,
                R.drawable.menu_system_setting*/} ;
    }

    /**
     * 查询集合布局
     */
    public interface Home_Enquiry{
        public static final int[] imgs = {
                /*R.drawable.menu_query_ec,
                R.drawable.menu_query_cash*/} ;
    }

    /**
     * 预授权集合布局
     */
    public interface Home_Preauth{
        public static final int[] imgs = {
                /*R.drawable.menu_preauth,
                R.drawable.menu_preauth_void,
                R.drawable.menu_preauth_comp,
                R.drawable.menu_preauth_comp_void*/} ;
    }

    /**
     * 管理类集合布局
     */
    public interface Home_Manage{
        public static final int[] imgs = {
                /*R.drawable.menu_manage_logon,
                R.drawable.menu_manage_logout,
                R.drawable.menu_manage_settle,
                R.drawable.menu_manage_download,
                R.drawable.menu_manage_history*/} ;
    }

    /**
     * 所有菜单标记
     */
    public interface Menus{
        public static final String VOID = "消费撤销" ;
        public static final String ENQUIRY = "查询" ;
        public static final String ENQUIRY_1 = "余额查询" ;
        public static final String ENQUIRY_2 = "电子现金余额查询" ;
        public static final String PREAUTH = "预授权交易" ;
        public static final String PREAUTH_1 = "预授权" ;
        public static final String PREAUTH_2 = "预授权完成" ;
        public static final String PREAUTH_3 = "预授权撤销" ;
        public static final String PREAUTH_4 = "预授权完成撤销" ;
        public static final String MANAGER = "管理" ;
        public static final String MANAGER_1 = "签到" ;
        public static final String MANAGER_2 = "签退" ;
        public static final String MANAGER_3 = "结算" ;
        public static final String MANAGER_4 = "参数公钥下载" ;
        public static final String MANAGER_5 = "历史交易查询" ;
        public static final String APPSTORE = "融商店" ;
        public static final String PROSETTING = "程序设置" ;
        public static final String SYSSETTING = "系统设置" ;
        public static final String QUERYTRANSDETAILS = "查询交易明细" ;
        public static final String REPRINTLAST = "重打最后一笔" ;
        public static final String REPRINTALL = "打印交易明细" ;
    }

    public static final String[] MenusCN = {
        "消费撤销",
        "查询",
        "余额查询",
        "电子现金余额查询",
        "预授权交易",
        "预授权",
        "预授权完成" ,
        "预授权撤销",
        "预授权完成撤销",
        "管理",
        "签到",
        "签退",
        "结算" ,
        "参数公钥下载",
        "历史交易查询",
        "融商店",
        "程序设置",
        "系统设置",
        "查询交易明细",
        "重打最后一笔",
        "打印交易明细",
        "扫码消费",
        "扫码撤销",
        "扫码退货",
    };

    public static final String[] MenusEN = {
        "Void",
        "Balances",
        "Balance",
        "EC_Balance",
        "Pre-auths",
        "Preauth",
        "Pre-Completed",
        "Pre-Void",
        "Pre-CompVoid",
        "Management",
        "SignIn",
        "SignOut",
        "Settle",
        "DownParasCapks",
        "HistoryTrans",
        "RongAppstore",
        "AppSettings",
        "SysSettings",
        "QueryTransDetails",
        "ReprintLastTrans",
        "PrintTransDetails",
        "Scan-Sale",
        "Scan-Void",
        "Scan-Refund",
    };

    /**
     * 设置布局
     */
    public interface Settings{
        public static final int[] IMGS = {
                R.drawable.icon_setting_communication,
                R.drawable.icon_setting_transpara,
                R.drawable.icon_setting_keyspara,
                R.drawable.icon_setting_maintainpdw,
                R.drawable.icon_setting_errlogs,
                R.drawable.icon_setting_privacy,
                R.drawable.icon_setting_deviceinfo
        };

        public static final int[] IMGS2 = {
                R.drawable.home2_setting_commun,
                R.drawable.home2_setting_trans,
                //R.drawable.home2_setting_keys,
                //R.drawable.home2_setting_privacy
                R.drawable.home2_setting_android
        } ;
    }


    public interface InputTitle{
        public static final int[] TITLEs = {
                R.string.please_input_amount,
                R.string.please_input_master_pass,
                R.string.please_input_trace_no,
                R.string.please_input_auth_code,
                R.string.please_input_data_time,
                R.string.please_input_reference,
        };
    }
}
