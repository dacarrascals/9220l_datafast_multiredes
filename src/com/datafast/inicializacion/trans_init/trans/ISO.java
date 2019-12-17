package com.datafast.inicializacion.trans_init.trans;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Elkin Beltrán
 */
public class ISO {
    public static String ventaOnLine      =	"00";
    public static String ventaOffLine     = "01";
    public static String transReverso     = "02";
    public static String transAnulacion   =	"03";
    public static String transBatch		  =	"05";
    public static String consultaVentas	  =	"06";
    public static String pagos			  =	"07";
    public static String consulta_ticket  =	"08";
    public static String loginTrans    	  =	"09";
    public static String transCierre	  =	"10";
    public static String RepParCaja       = "11";
    public static String InfFinSort       = "12";
    public static String RepCierre	      = "13";
    public static String ListaNumeros     = "14";
    public static String TicketGanadores  = "15";
    public static String idConsultaSincro = "17";
    public static String idRepOtroProductos = "27";

    public static String separadorPipe    = "|";
    boolean isoModificado=true;
    public class fieldDefinition_t
    {
        int typeLenght;
		int size;
		int typeContent;

        public fieldDefinition_t(int typeLenght, int typeContent, int size)
        {
           this.typeLenght = typeLenght;
           this.size = size;
           this.typeContent = typeContent;
        }
    }

    public class fieldDecode_t
    {
        private int start;
		private int size;
		public boolean exist;

        fieldDecode_t(int start, int size, boolean exist)
        {
           this.start = start;
           this.size = size;
           this.exist = exist;
        }
    }

    fieldDecode_t[] fieldDecode = new fieldDecode_t[135];

    fieldDefinition_t[] fieldDefinition = new fieldDefinition_t[135];;

    public byte FLLVAR = 0x01;
    public byte FLLLVAR = 0x02;
    public byte FIX = 0x04;
    public byte ATTN = 0x10;
    public byte ATTAN = 0x20;
    public byte ATTANS = 0x40;

    public static final int typeLenghtFIX = 0;
    public static final int typeLenghtFLLVAR = 1;
    public static final int typeLenghtFLLLVAR = 2;
    public static final int typeLenghtFHHHVAR = 3;

    public static final int typeContentATTN = 0;
    public static final int typeContentATTAN = 1;
    public static final int typeContentATTANS = 2;

    public static final int lenghtNotInclude = 0;
    public static final int lenghtInclude = 1;

    public static final int TpdunotInclude = 0;
    public static final int TpduInclude = 1;

    public static final int startField = 0;
    public static final int field_01_BITMAP_EXTENDED = 1;
    public static final int field_02_PRIMARY_ACCOUNT_NUMBER = 2;
    public static final int field_03_PROCESSING_CODE= 3;
    public static final int field_04_AMOUNT_TRANSACTION= 4;
    public static final int field_05_AMOUNT_SETTLEMENT = 5;
    public static final int field_06_AMOUNT_CARDHOLDER_BILLING =6;
    public static final int field_07_TRANSMISSION_DATE_TIME_MMDDHHMMSS = 7;
    public static final int field_08_AMOUNT_CARDHOLDER_BILLING_FEE = 8;
    public static final int field_09_CONVERSION_RATE_SETTLEMENT = 9;
    public static final int field_10_CONVERSION_RATE_CARD_HOLDER_BILLING = 10;
    public static final int field_11_SYSTEMS_TRACE_AUDIT_NUMBER = 11;
    public static final int field_12_TIME_LOCAL_TRANSACTION_HHMMSS = 12;
    public static final int field_13_DATE_LOCAL_TRANSACTION_MMDD = 13;
    public static final int field_14_DATE_EXPIRATION_YYMM = 14;
    public static final int field_15_DATE_SETTLEMENT_MMDD = 15;
    public static final int field_16_DATE_CONVERSION_MMDD = 16;
    public static final int field_17_DATE_CAPTURE = 17;
    public static final int field_18_MERCHANTS_TYPE = 18;
    public static final int field_19_ACQUIRING_INSTITUTION_COUNTRY_CODE = 19;
    public static final int field_20_PRIMARY_ACCOUNT_NUMBER_EXTENDED_COUNTRY_CODE = 20;
    public static final int field_21_FORWARDING_INSTITUTION_COUNTRY_CODE = 21;
    public static final int field_22_POINT_OF_SERVICE_ENTRY_MODE = 22;
    public static final int field_23_CARD_SEQUENCE_NUMBER = 23;
    public static final int field_24_NETWORK_INTERNATIONAL_IDENTIFIER = 24;
    public static final int field_25_POINT_OF_SERVICE_CONDITION_CODE = 25;
    public static final int field_26_POINT_OF_SERVICE_PIN_CAPTURE_CODE = 26;
    public static final int field_27_AUTHORIZATION_IDENTIFICATION_RESPONSE_LENGHT = 27;
    public static final int field_28_AMOUNT_TRANSACTION_FEE_X_N = 28;
    public static final int field_29_AMOUNT_SETTLEMENT_FEE_X_N = 29;
    public static final int field_30_AMOUNT_TRANSACTION_PROCESSING_FEE_X_N = 30;
    public static final int field_31_AMOUNT_SETTLEMENT_PROCESSING_FEE = 31;
    public static final int field_32_ACQUIRING_INSTITUTION_IDENTIFICATION_CODE = 32;
    public static final int field_33_FORWARDING_INSTITUTION_IDENTIFICATION_CODE = 33;
    public static final int field_34_PRIMARY_ACCOUNT_NUMBER_EXTENDED = 34;
    public static final int field_35_TRACK_2_DATA = 35;
    public static final int field_36_TRACK_3_DATA = 36;
    public static final int field_37_RETRIEVAL_REFERENCE_NUMBER = 37;
    public static final int field_38_AUTHORIZATION_IDENTIFICATION_RESPONSE = 38;
    public static final int field_39_RESPONSE_CODE = 39;
    public static final int field_40_SERVICE_RESTRICTION_CODE = 40;
    public static final int field_41_CARD_ACCEPTOR_TERMINAL_IDENTIFICATION = 41;
    public static final int field_42_CARD_ACCEPTOR_IDENTIFICATION_CODE = 42;
    public static final int field_43_CARD_ACCEPTOR_NAME_LOCATION = 43;
    public static final int field_44_ADDITIONAL_RESPONSE_DATA = 44;
    public static final int field_45_TRACK_1_DATA = 45;
    public static final int field_46_ADDITIONAL_DATA_ISO = 46;
    public static final int field_47_ADDITIONAL_DATA_NATIONAL = 47;
    public static final int field_48_ADDITIONAL_DATA_PRIVATE = 48;
    public static final int field_49_CURRENCY_CODE_TRANSACTION = 49;
    public static final int field_50_CURRENCY_CODE_SETTLEMENT = 50;
    public static final int field_51_CURRENCY_CODE_CARD_HOLDER_BILLING = 51;
    public static final int field_52_PERSONAL_IDENTIFICATION_NUMBER_PIN_DATA = 52;
    public static final int field_53_SECURITY_RELATED_CONTROL_INFORMATION = 53;
    public static final int field_54_ADDITIONAL_AMOUNTS = 54;
    public static final int field_55_RESERVED_ISO = 55;
    public static final int field_56_RESERVED_ISO = 56;
    public static final int field_57_RESERVED_NATIONAL = 57;
    public static final int field_58_RESERVED_NATIONAL = 58;
    public static final int field_59_RESERVED_NATIONAL = 59;
    public static final int field_60_RESERVED_PRIVATE = 60;
    public static final int field_61_RESERVED_PRIVATE = 61;
    public static final int field_62_RESERVED_PRIVATE = 62;
    public static final int field_63_RESERVED_PRIVATE = 63;
    public static final int field_64_MESSAGE_AUTHENTICATION_CODE = 64;
    public static final int endISOField = 66;
    public static final int e_1_LENGHT = 66;
    public static final int e_2_ID_TPDU = 67;
    public static final int e_3_DESTINATION = 68;
    public static final int e_4_SOURCE = 69;
    public static final int e_5_MESSAGE_TYPE = 70;
    public static final int e_6_PRIMARY_BITMAP = 71;
    public static final int e_7_SECONDARY_BITMAP = 72;
    public static final int endField = 73;

    private byte TRUE = 0x01;
    private byte FALSE = 0x00;
    private byte[] input;
    private byte includeLenght = TRUE;
    private byte includeTpdu = TRUE;
    private int pointerInput = 0;
    private byte[] inputBitmap = new byte[8];
    private int pointerOutput = 0;
    private int pointerBitmapOutput = 0;
    private byte[] bitmapOutput = new byte[8];
    private byte[] output = new byte[2048];

    private void copyBytes(byte[] source,
                          byte[] destination,
                          int startSource,
                          int startDestination,
                          int numOfBytes)
    {
       int i, j = 0;
       for (i = startSource; i < startSource + numOfBytes; i++)
       {
           destination[startDestination + j] = source[startSource + j];
           j += 1;
       }
    }

    public boolean setTPDUId(String id)
    {
	   try
	   {
		 byte[] tmp = null;
		 int size = 0;
                 HexEncoding strToByte = new HexEncoding();
	         tmp = strToByte.GetBytes(id);
                 size = tmp.length;
                 if (size == 1)
	         {
	           System.arraycopy(tmp, 0, output,pointerOutput, size);
	           pointerOutput += size;
	           return true;
	         }
	         else
	          return false;
            }
	    catch (Exception ex)
	    {
		ex.printStackTrace();
                return false;
	    }
    }

    public boolean setTPDUId(byte[] id)
    {
	try
	{
	  int size = 0;
	  size = id.length;
	  if (size == 1)
	  {
	     System.arraycopy(id, 0, output,pointerOutput, size);
	     pointerOutput += size;
	     return true;
	  }
	  else
	     return false;
	}
	catch (Exception ex)
	{
	  ex.printStackTrace();
          return false;
	}
    }


    public boolean setTPDUSource(String source)
    {
	try
	{
	  byte[] tmp = null;
	  int size = 0;
	  int discard = 0;
          HexEncoding strToByte = new HexEncoding();
          tmp = strToByte.GetBytes(source);
	  size = tmp.length;
          if ( (size == 2) && (discard == 0) )
          {
            System.arraycopy(tmp, 0, output,pointerOutput, size);
	    pointerOutput += size;
	    return true;
	  }
	  else
	    return false;
	}
	catch (Exception ex)
	{
	  ex.printStackTrace();
          return false;
	}
    }


    public boolean setTPDUSource(byte[] source)
    {
	try
	{
	  //byte[] tmp = null;
	  int size = 0;
	  size = source.length;
          if (size == 2)
          {
             System.arraycopy(source, 0, output,pointerOutput, size);
             pointerOutput += size;
             return true;
          }
          else
             return false;
     	}
	catch (Exception ex)
	{
	  ex.printStackTrace();
          return false;
	}
    }

    public boolean setTPDUDestination(String destination)
    {
	try
	{
	  byte[] tmp = null;
	  int size = 0;
	  int discard = 0;
          HexEncoding strToByte = new HexEncoding();
          tmp = strToByte.GetBytes(destination);
	  size = tmp.length;
          if ( (size == 2) && (discard == 0) )
          {
              System.arraycopy(tmp, 0, output,pointerOutput, size);
              pointerOutput += size;
              return true;
          }
          else
              return false;
        }
	catch (Exception ex)
	{
	  ex.printStackTrace();
          return false;
	}
    }

    public boolean setTPDUDestination(byte[] destination)
    {
        try
	{
	  int size = 0;
	  size = destination.length;
	  if (size == 2)
          {
                  System.arraycopy(destination, 0, output,pointerOutput, size);
	          pointerOutput += size;
	          return true;
          }
          else
                  return false;
          }
	  catch (Exception ex)
	  {
	     ex.printStackTrace();
             return false;
	  }
    }


    public boolean setMsgType(String msgType)
    {
          try
	  {
	    byte[] tmp = new byte[2];
	    HexEncoding strToByte = new HexEncoding();
	    tmp = strToByte.GetBytes(msgType);
	    if (msgType.length() == 4)
	    {
	       System.arraycopy(tmp,0,output,pointerOutput,2);
	       pointerOutput += 2;
	       pointerBitmapOutput = pointerOutput;
	       pointerOutput += 8;
	       return true;
	    }
	    else
	    {
	       pointerOutput += 2;
	       pointerBitmapOutput = pointerOutput;
	       pointerOutput += 8;
	       return false;
	    }
	  }
	  catch (Exception ex)
	  {
	    pointerOutput += 2;
	    pointerBitmapOutput = pointerOutput;
	    pointerOutput += 8;
	    return false;
	  }
    }

    public boolean setField(int id, byte[] content)
    {
        try
	{
            int size = 0;
	    int field = 0;
	    field = (int) id - 1;
	    int flag = 0x80;
	    bitmapOutput[ field / 8 ] |= (byte) (flag >> (field%8));
	    if ((fieldDefinition[(int)id].typeLenght == (byte) ISO.typeLenghtFIX) &&
	         (fieldDefinition[(int)id].typeContent == (byte) ISO.typeContentATTN))
	    {
	      if ((fieldDefinition[(int)id].size % 2) != 0)
		     size = (fieldDefinition[(int)id].size + 1)/ 2;
	      else
	         size = fieldDefinition[(int)id].size / 2;
	      if (content.length == size)
	      {
	         System.arraycopy(content, 0, output,pointerOutput, content.length);
	         pointerOutput += size;
	         return true;
	      }
	      else
	         return false;
	     }
	     else if ((fieldDefinition[(int)id].typeLenght == (byte) ISO.typeLenghtFIX) &&
	              (fieldDefinition[(int)id].typeContent == (byte) ISO.typeContentATTAN))
	     {
	        size = fieldDefinition[(int)id].size;
	        if (content.length == size)
	        {
	          System.arraycopy(content, 0, output,pointerOutput, content.length);
	          pointerOutput += size;
	          return true;
	        }
	        else
	          return false;
	     }
	     else if ((fieldDefinition[(int)id].typeLenght == (byte) ISO.typeLenghtFLLLVAR) &&
	              (fieldDefinition[(int)id].typeContent == (byte) ISO.typeContentATTAN))
	     {
	        if (content.length <= 999)
	        {
	           pointerOutput = SetLenght(output, pointerOutput, (int) content.length);
	           System.arraycopy(content, 0, output,pointerOutput, content.length);
	           pointerOutput += content.length;
	           return true;
	        }
	        else
               return false;
	     }
	     return true;
	   }
	  catch (Exception ex)
	  {
	     ex.printStackTrace();
             return false;
	  }
   }

   public boolean setField(int id, String content)
   {
	   try
	   {
	   	 int size = 0;
	   	 HexEncoding toByte = new HexEncoding();
	   	 //calculamos el bitmap
	   	 int field = 0;
	   	 field = (int) id - 1;
	   	 int flag = 0x80;
	   	 bitmapOutput[ field / 8 ] |= (byte) (flag >> (field%8));
                 byte[] tmp = null;
	         if ((fieldDefinition[(int)id].typeLenght == (byte) ISO.typeLenghtFIX) &&
	             (fieldDefinition[(int)id].typeContent == (byte) ISO.typeContentATTN))
	         {
	             if ((fieldDefinition[(int)id].size % 2) != 0)
		         size = (fieldDefinition[(int)id].size + 1)/ 2;
	             else
	                 size = fieldDefinition[(int)id].size / 2;
	             tmp = toByte.GetBytes(content);
	             if (tmp.length == size)
	             {
	                 System.arraycopy(tmp, 0, output,pointerOutput, tmp.length);
	                 pointerOutput += size;
	                 return true;
	             }
	             else
	                 return false;
	         }
	         else if ((fieldDefinition[(int)id].typeLenght == (byte) ISO.typeLenghtFIX) &&
	                  (fieldDefinition[(int)id].typeContent == (byte) ISO.typeContentATTAN))
	         {
	             size = fieldDefinition[(int)id].size;
	             tmp = content.getBytes();
	             if (tmp.length == size)
	             {
	                 System.arraycopy(tmp, 0, output,pointerOutput, tmp.length);
	                 pointerOutput += size;
	                 return true;
	             }
	             else
	                 return false;
	         }
	         else if ((fieldDefinition[(int)id].typeLenght == (byte) ISO.typeLenghtFLLLVAR) &&
	                  (fieldDefinition[(int)id].typeContent == (byte) ISO.typeContentATTAN))
	         {
	             tmp = content.getBytes();
	             if (tmp.length <= 999)
	             {
	                  pointerOutput = SetLenght(output, pointerOutput, (int) tmp.length);
	                  System.arraycopy(tmp, 0, output,pointerOutput, tmp.length);
	                  pointerOutput += tmp.length;
	                  return true;
	             }
	             else
                          return false;
	         }
	         return true;
	   }
	  catch (Exception ex)
	  {
	     ex.printStackTrace();
             return false;
	  }
   }
     
   public byte[] getTxnOutput()
   {
	  try
	  {
	    byte[] tmp = new byte[pointerOutput];
	    System.arraycopy(output,0,tmp,0,pointerOutput);
	    if (includeLenght == TRUE)
	    {
	      tmp[0] = (byte) ((pointerOutput - 2) / 256);
	      tmp[1] = (byte) ((pointerOutput - 2) % 256);
	    }
	    //Copiamos el bitmap
	    System.arraycopy(bitmapOutput,0,tmp,pointerBitmapOutput,8);
	    return tmp;
	  }
	  catch (Exception ex)
	  {
	     ex.printStackTrace();
             return null;
	  }
   }

   private void decode()
   {
       int i = 0;
       int mask = 0x80;
       int idFld = 0, idByte = 0;
       for (i = 0; i < (int) endField; i ++)
       {
           fieldDecode[i].start = 0;
           fieldDecode[i].size = 0;
           fieldDecode[i].exist = false;
       }
       //lenght
       if (includeLenght == TRUE)
       {
         fieldDecode[(int)e_1_LENGHT].start = 0;
         fieldDecode[(int)e_1_LENGHT].size = 2;
         fieldDecode[(int)e_1_LENGHT].exist = true;
         pointerInput += 2;
       }
       //tpdu
       if (includeTpdu == TRUE)
       {
         fieldDecode[(int)e_2_ID_TPDU].start = pointerInput;
         fieldDecode[(int)e_2_ID_TPDU].size = 1;
         fieldDecode[(int)e_2_ID_TPDU].exist = true;
         pointerInput += 1;
         fieldDecode[(int)e_3_DESTINATION].start = pointerInput;
         fieldDecode[(int)e_3_DESTINATION].size = 2;
         fieldDecode[(int)e_3_DESTINATION].exist = true;
         pointerInput += 2;
         fieldDecode[(int)e_4_SOURCE].start = pointerInput;
         fieldDecode[(int)e_4_SOURCE].size = 2;
         fieldDecode[(int)e_4_SOURCE].exist = true;
         pointerInput += 2;
       }
       //Message type
       fieldDecode[(int)e_5_MESSAGE_TYPE].start = pointerInput;
       fieldDecode[(int)e_5_MESSAGE_TYPE].size = 2;
       fieldDecode[(int)e_5_MESSAGE_TYPE].exist = true;
       pointerInput += 2;
       //Primary BitMap
       fieldDecode[(int)e_6_PRIMARY_BITMAP].start = pointerInput;
       fieldDecode[(int)e_6_PRIMARY_BITMAP].size = 8;
       fieldDecode[(int)e_6_PRIMARY_BITMAP].exist = true;
       copyBytes(input, inputBitmap, pointerInput, 0, 8);
       pointerInput += 8;
       //Decode all fields
       for (i = (int) field_01_BITMAP_EXTENDED; i < (int) endISOField; i ++)
       {
           if (i == (int) field_01_BITMAP_EXTENDED)
           {
              idFld += 1;
              continue;
           }
           if ((mask & inputBitmap[idByte]) != 0)
           {
               pointerInput = DecodeField(idFld, pointerInput);
           }
           mask = (byte) (mask >> 1);
           if (mask == 0)
           {
              mask = 0x80;
              idByte += 1;
           }
           idFld += 1;
       }

   }

   private int DecodeField(int field, int pointer)
   {
	   int size = 0;
	   if ((fieldDefinition[field].typeLenght == (byte) ISO.typeLenghtFIX) &&
	       (fieldDefinition[field].typeContent == (byte) ISO.typeContentATTN))
	   {
	      if ((fieldDefinition[field].size % 2) != 0)
		    size = (fieldDefinition[field].size + 1)/ 2;
	      else
	        size = fieldDefinition[field].size / 2;
	   }
	   else if ((fieldDefinition[field].typeLenght == (byte) ISO.typeLenghtFIX) &&
	            (fieldDefinition[field].typeContent == (byte) ISO.typeContentATTAN))
	      size = fieldDefinition[field].size;
	   else if ((fieldDefinition[field].typeLenght == (byte) ISO.typeLenghtFLLLVAR) &&
	            (fieldDefinition[field].typeContent == (byte) ISO.typeContentATTAN))
	   {
	      size = GetLenght(input, pointer,ISO.typeLenghtFLLLVAR);
              pointer += 2;
	   }
	   else if ((fieldDefinition[field].typeLenght == (byte) ISO.typeLenghtFLLVAR) &&
	            (fieldDefinition[field].typeContent == (byte) ISO.typeContentATTN))
	   {
	      size = GetLenght(input,pointer,ISO.typeLenghtFLLVAR);
              pointer += 1;
	      if ((size % 2) != 0)
		    size = (size + 1)/ 2;
	      else
	        size = size / 2;
	   }
	   else if((fieldDefinition[field].typeLenght == (byte) ISO.typeLenghtFHHHVAR))
       {
           size = GetLenght(input,pointer,ISO.typeLenghtFHHHVAR);
           pointer+=2;
       }
	   else
	      size = fieldDefinition[field].size;
	   fieldDecode[field].exist = true;
	   fieldDecode[field].start = pointer;
	   fieldDecode[field].size = size;
	   pointer += size;
           return pointer;
   }

    public int getSizeField(int field)
    {
        return fieldDecode[field].size;
    }
   public String GetField(int id)
   {
	   try
	   {
	      String field = "";
              HexEncoding toHex = new HexEncoding();
	      if ( (id == e_1_LENGHT) ||
                   (id == e_2_ID_TPDU) ||
                   (id == e_3_DESTINATION) ||
                   (id == e_4_SOURCE) ||
                   (id == e_5_MESSAGE_TYPE) ||
                   (id == e_6_PRIMARY_BITMAP)
                   )
	      {
	         field = toHex.hexString(input, fieldDecode[(int)id].start,fieldDecode[(int)id].size);
	      }
	      else if ((fieldDefinition[(int)id].typeLenght == (byte) ISO.typeLenghtFIX) &&
	         (fieldDefinition[(int)id].typeContent == (byte) ISO.typeContentATTN))
	      {
	         field = toHex.hexString(input, fieldDecode[(int)id].start,fieldDecode[(int)id].size);
	      }
	      else if ((fieldDefinition[(int)id].typeLenght == (byte) ISO.typeLenghtFIX) &&
	            (fieldDefinition[(int)id].typeContent == (byte) ISO.typeContentATTAN))
	      {
	         field = toHex.getString(input,fieldDecode[(int)id].start,fieldDecode[(int)id].size);

	      }
	      else if ((fieldDefinition[(int)id].typeLenght == (byte) ISO.typeLenghtFLLLVAR) &&
	            (fieldDefinition[(int)id].typeContent == (byte) ISO.typeContentATTAN))
	      {
	         field = toHex.getString(input,fieldDecode[(int)id].start,fieldDecode[(int)id].size);
	      }
	      else if ((fieldDefinition[(int)id].typeLenght == (byte) ISO.typeLenghtFLLVAR) &&
	             (fieldDefinition[(int)id].typeContent == (byte) ISO.typeContentATTN))
	      {
	         field = toHex.hexString(input, fieldDecode[(int)id].start,fieldDecode[(int)id].size);
	      }
          else if ((fieldDefinition[(int)id].typeLenght == (byte) ISO.typeLenghtFHHHVAR) &&
                  (fieldDefinition[(int)id].typeContent == (byte) ISO.typeContentATTAN))

          {
              field = toHex.getString(input,fieldDecode[(int)id].start,fieldDecode[(int)id].size);
          }
	      return field;
	   }
	  catch (Exception ex)
	  {
	     ex.printStackTrace();
             return null;
	  }
   }


   public byte[] GetFieldB(int id)
   {
	   try
	   {
	     byte[] outputField = null;
	     if (fieldDecode[(int)id].exist == true)
	     {
	        outputField = new byte[fieldDecode[(int)id].size];
	        System.arraycopy(input,(int)fieldDecode[(int)id].start,outputField,0,(int)fieldDecode[(int)id].size);
	     }
	     return outputField;
	   }
	   catch (Exception ex)
	   {
	     ex.printStackTrace();
             return null;
	   }
    }


   public int GetLenght(byte[] data, int start, int typeLen)
   {
      int len = 0;
      try
      {
        if (typeLen == ISO.typeLenghtFLLLVAR)
        {
          len = (((data[start] & 0xF0) >> 4) * 1000 ) + ((data[start] & 0x0F) * 100 ) + (((data[start + 1] & 0xF0) >> 4) * 10 ) + (data[start + 1] & 0x0F );
        }
        else if (typeLen == ISO.typeLenghtFLLVAR)
        {
          len = (((data[start] & 0xF0) >> 4) * 10 ) + (data[start] & 0x0F);
        }
        else if(typeLen == ISO.typeLenghtFHHHVAR)
        {
            int result = 0;
            int lenField;
            int lenField1;

            lenField=data[start];
            lenField1=data[start+1];

            if(lenField<0)
            {
                lenField+=256;
            }
            if(lenField1<0)
            {
                lenField1+=256;
            }
            result = lenField*256;
            result+=lenField1;
            len= result;
        }
        return len;
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
        return 0;
      }
   }

   public int SetLenght(byte[] txn, int pos, int len)
   {
      txn[pos] = (byte)   (   (((( (len / 10) / 10 ) / 10 ) & 0x0F) << 4)    | ((( (len / 10) / 10 ) % 10 ) & 0x0F)     ) ;
      txn[pos + 1] = (byte)   (((((len / 10) % 10 ) & 0x0F) << 4) | (len % 10));
      pos+=2;
      return pos;
   }

   public byte[] getTime()
   {
	byte[] tmp = null;
	HexEncoding toByte = new HexEncoding();

        try
        {
	    //Arreglar hora en java
            //String auxStr = System.DateTime.Now.Hour.ToString().PadLeft(2,'0') + System.DateTime.Now.Minute.ToString().PadLeft(2,'0') + System.DateTime.Now.Second.ToString().PadLeft(2,'0');
	    String auxStr = timeStr();
            tmp = toByte.GetBytes(auxStr);
	    if (tmp.length == 3)
	       return tmp;
	    else
	       return null;
	}
        catch (Exception ex)
        {
           ex.printStackTrace();
           return null;
        }
   }

   public byte[] getDate()
   {
	byte[] tmp = null;
	HexEncoding toByte = new HexEncoding();

	try
        {
	    //Arreglar fecha en java
            //string auxStr = System.DateTime.Now.Month.ToString().PadLeft(2,'0') + System.DateTime.Now.Day.ToString().PadLeft(2,'0');
            String auxStr = dateStr();
	    tmp = toByte.GetBytes(auxStr);
	    if (tmp.length == 2)
	       return tmp;
	    else
	       return null;
	}
        catch (Exception ex)
        {
           ex.printStackTrace();
           return null;
        }
   }


    public ISO(byte[] input, int lenght, int tpdu)
    {
                initIsoDefinition(fieldDefinition);
                initFieldDecode();
		this.input = input;
		this.includeLenght = (byte) lenght;
		this.includeTpdu = (byte) tpdu;
		pointerOutput = 0;
		if (lenght == ISO.lenghtInclude)
		   pointerOutput += 2;
		//if (tpdu == ISO.Tpdu.include)
		//   pointerOutput += 5;
		decode();
    }

    public ISO(int lenght, int tpdu)
    {
        initIsoDefinition(fieldDefinition);
	this.includeLenght = (byte) lenght;
	this.includeTpdu = (byte) tpdu;
	pointerOutput = 0;
	if (lenght == ISO.lenghtInclude)
	   pointerOutput += 2;
    }

    public void initFieldDecode()
    {
        int i;
        for (i = 0; i<endField; i++)
        {
            fieldDecode[i]=new fieldDecode_t(0,0,false);
        }
    }

    public void initIsoDefinition(fieldDefinition_t[] isoField )
    {
          //processing code
          isoField[(int)field_03_PROCESSING_CODE] = new fieldDefinition_t(ISO.typeLenghtFIX,
                                                                                                    ISO.typeContentATTN,
                                                                                                    6);
          //amount
          isoField[(int)field_04_AMOUNT_TRANSACTION] = new fieldDefinition_t(ISO.typeLenghtFIX,
                                                                                                       ISO.typeContentATTN,
                                                                                                       12);
          //TIMEDATE
          isoField[(int)field_07_TRANSMISSION_DATE_TIME_MMDDHHMMSS] = new fieldDefinition_t(ISO.typeLenghtFIX,
                                                                                                               ISO.typeContentATTN,
                                                                                                               10);          
          //stan
          isoField[(int)field_11_SYSTEMS_TRACE_AUDIT_NUMBER] = new fieldDefinition_t(ISO.typeLenghtFIX,
                                                                                                               ISO.typeContentATTN,
                                                                                                               6);
          //time
          isoField[(int)field_12_TIME_LOCAL_TRANSACTION_HHMMSS] = new fieldDefinition_t(ISO.typeLenghtFIX,
                                                                                                                  ISO.typeContentATTN,
                                                                                                                  6);
          //date
        if(isoModificado==true)
        {
            isoField[(int) field_13_DATE_LOCAL_TRANSACTION_MMDD] = new fieldDefinition_t(ISO.typeLenghtFIX,
                    ISO.typeContentATTN,
                    8);
        }
        else {
            isoField[(int) field_13_DATE_LOCAL_TRANSACTION_MMDD] = new fieldDefinition_t(ISO.typeLenghtFIX,
                    ISO.typeContentATTN,
                    4);
        }

        //nii
          isoField[(int)field_24_NETWORK_INTERNATIONAL_IDENTIFIER] = new fieldDefinition_t(ISO.typeLenghtFIX,
                                                                                                                     ISO.typeContentATTN,
                                                                                                                     4);
          //field35
          isoField[(int)field_35_TRACK_2_DATA] = new fieldDefinition_t(ISO.typeLenghtFLLVAR,
                                                                                                 ISO.typeContentATTN,
                                                                                                 8);

          //rrn
          isoField[(int)field_37_RETRIEVAL_REFERENCE_NUMBER] = new fieldDefinition_t(ISO.typeLenghtFIX,
                                                                                                                      ISO.typeContentATTAN,
                                                                                                                      12);
          
          //Authorization id
          isoField[(int)field_38_AUTHORIZATION_IDENTIFICATION_RESPONSE] = new fieldDefinition_t(ISO.typeLenghtFIX,
                                                                                                                          ISO.typeContentATTAN,
                                                                                                                          8);
          //Response code
          isoField[(int)field_39_RESPONSE_CODE] = new fieldDefinition_t(ISO.typeLenghtFIX,
                                                                                                  ISO.typeContentATTAN,
                                                                                                  2);
          //terminal id
          isoField[(int)field_41_CARD_ACCEPTOR_TERMINAL_IDENTIFICATION] = new fieldDefinition_t(ISO.typeLenghtFIX,
                                                                                                                          ISO.typeContentATTAN,
                                                                                                                          8);
          //acquirer id
          isoField[(int)field_42_CARD_ACCEPTOR_IDENTIFICATION_CODE] = new fieldDefinition_t(ISO.typeLenghtFIX,
                                                                                                                      ISO.typeContentATTAN,
                                                                                                                      15);
          //field47  (additional iva...)
          isoField[(int)field_47_ADDITIONAL_DATA_NATIONAL] = new fieldDefinition_t(ISO.typeLenghtFLLLVAR,
                                                                                                             ISO.typeContentATTAN,
                                                                                                             8);
          //field48
          isoField[(int)field_48_ADDITIONAL_DATA_PRIVATE] = new fieldDefinition_t(ISO.typeLenghtFLLLVAR,
                                                                                                     ISO.typeContentATTAN,
                                                                                                     8);          
          
          //field54
          isoField[(int)field_54_ADDITIONAL_AMOUNTS] = new fieldDefinition_t(ISO.typeLenghtFLLLVAR,
                                                                                                       ISO.typeContentATTAN,
                                                                                                       8);
          //PinBlock
          isoField[(int)field_52_PERSONAL_IDENTIFICATION_NUMBER_PIN_DATA] = new fieldDefinition_t(ISO.typeLenghtFIX,
                                                                                                                            ISO.typeContentATTN,
                                                                                                                            16);
          //field54
          isoField[(int)field_54_ADDITIONAL_AMOUNTS] = new fieldDefinition_t(ISO.typeLenghtFLLLVAR,
                                                                                                       ISO.typeContentATTAN,
                                                                                                       8);
          //field60
          isoField[(int)field_60_RESERVED_PRIVATE] = new fieldDefinition_t(ISO.typeLenghtFLLLVAR,
                                                                                                     ISO.typeContentATTAN,
                                                                                                     8);
          //field61
          isoField[(int)field_61_RESERVED_PRIVATE] = new fieldDefinition_t(ISO.typeLenghtFLLLVAR,
                                                                                                     ISO.typeContentATTAN,
                                                                                                     8);
          //field62
          isoField[(int)field_62_RESERVED_PRIVATE] = new fieldDefinition_t(ISO.typeLenghtFLLLVAR,
                                                                                                     ISO.typeContentATTAN,
                                                                                                     8);
          //field63
          isoField[(int)field_63_RESERVED_PRIVATE] = new fieldDefinition_t(ISO.typeLenghtFLLLVAR,
                                                                                                     ISO.typeContentATTAN,
                                                                                                     8);

        //field64
        isoField[(int)field_64_MESSAGE_AUTHENTICATION_CODE] = new fieldDefinition_t(ISO.typeLenghtFHHHVAR,
                ISO.typeContentATTAN,
                8);
    }

    public String timeStr(){
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);                       
        //cal.setTimeZone(TimeZone.getTimeZone("GMT-5"));

        int hr = cal.get(Calendar.HOUR);
        int mi = cal.get(Calendar.MINUTE);
        int se = cal.get(Calendar.SECOND);
        String hrC;
        String miC;
        String seC;
        
        ///Hora       
        if (cal.get(Calendar.AM_PM) == Calendar.AM ){
            if (hr == 0)
                hr=12;  ///Noon
            
            if (hr < 10){
                hrC = "0" + String.valueOf(hr);
            }
            else{
                hrC = String.valueOf(hr);
            }
        }
        else{
            hrC = String.valueOf(hr + 12); ///Se agregó para pointpay un + 1
        }
        
        ///Minuto
        if (mi < 10){
            miC = "0" + String.valueOf(mi);
        }
        else{
            miC = String.valueOf(mi);
        }

        ///Segundo
        if (se < 10){
            seC = "0" + String.valueOf(se);
        }
        else{
            seC = String.valueOf(se);
        }
        
        return hrC + miC + seC;
    }
    
    public String dateStr(){
        Calendar cal = Calendar.getInstance();
        int mm = cal.get(Calendar.MONTH) + 1;
        int dd = cal.get(Calendar.DAY_OF_MONTH);
        String mmC;
        String ddC;
        
        ///Mes
        if (mm < 10){
            mmC = "0" + String.valueOf(mm);
        }
        else{
            mmC = String.valueOf(mm);
        }
        
        ///Dia
        if (dd < 10){
            ddC = "0" + String.valueOf(dd);
        }
        else{
            ddC = String.valueOf(dd);
        }
        return mmC + ddC;
    }


    public static String dateYYYYMMDDStr(){
        Calendar cal = Calendar.getInstance();
        int mm = cal.get(Calendar.MONTH) + 1;
        int dd = cal.get(Calendar.DAY_OF_MONTH);
        int yyyy = cal.get(Calendar.YEAR);
        String mmC;
        String ddC;
        String yyyyC;

        ///Mes
        if (mm < 10){
            mmC = "0" + String.valueOf(mm);
        }
        else{
            mmC = String.valueOf(mm);
        }

        ///Dia
        if (dd < 10){
            ddC = "0" + String.valueOf(dd);
        }
        else{
            ddC = String.valueOf(dd);
        }

        //Año
        if (yyyy < 200)
            yyyy += 1900;

        yyyyC = String.valueOf(yyyy);

        return yyyyC + mmC + ddC;
    }

    public static int getStan()
    {
        return SharedPreferences.getValueIntPreference(Tools.getCurrentContext(),"STAN");
    }

    public static void incStan()
    {
        int stan;
        stan=SharedPreferences.getValueIntPreference(Tools.getCurrentContext(),"STAN");
        if(stan<999999)
            stan++;
        else stan=0;

        SharedPreferences.saveValueIntPreference(Tools.getCurrentContext(),SharedPreferences.KEY_STAN,stan);
    }

    public static void decStan()
    {
        int stan;
        stan=SharedPreferences.getValueIntPreference(Tools.getCurrentContext(),SharedPreferences.KEY_STAN);
        if(stan>0)
            stan--;
        else stan=0;

        SharedPreferences.saveValueIntPreference(Tools.getCurrentContext(),SharedPreferences.KEY_STAN,stan);
    }


}
