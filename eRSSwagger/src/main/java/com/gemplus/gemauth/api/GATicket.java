/**
 * GATicket.java
 *
 * Creation date: (2014-04-25 17:04:10)
 */

package com.gemplus.gemauth.api;



/**
 * NHS Spine Ticket API<BR>
 * Interface to the security broker and to GemAuthenticate.
 *
 * @author: Madhan Charles (Madhan.Charles@xml-solutions.com)
 */
public class GATicket extends java.lang.Object {
  /*
   * constants
   * =========
   */
  /** CONSTANT: Name of native library that this class is dependent on. */
  public static final String LIBRARY_NAME = "TicketApiDll";
  /** CONSTANT: Return value - Successful operation.*/
  public static final int TCK_API_SUCCESS = 0x00000000;
  /** CONSTANT: Return value - */
  public static final int TCK_API_ERR_INTERNAL = 0x00000001;
  /** CONSTANT: Return value - */
  public static final int TCK_API_NOTINSTALLED = 0x00000002;
  /** CONSTANT: Return value - */
  public static final int TCK_API_NOTINITIALIZED = 0x00000003;
  /** CONSTANT: Return value - */
  public static final int TCK_API_BUFFER_TO_SMALL = 0x00000004;
  /** CONSTANT: Return value - */
  public static final int TCK_API_NOSERVERFOUND = 0x00000005;
  /** CONSTANT: Return value - */
  public static final int TCK_API_VERSIONCONFLICT = 0x00000006;
  /** CONSTANT: Return value - */
  public static final int TCK_API_INVALID_INSTANCE_HANDLE = 0x00000007;
    /** CONSTANT: Return value - */
  public static final int TCK_API_USER_ABORT = 0x00000008;
    /** CONSTANT: Return value - */
  public static final int TCK_API_AUTHENTICATION_FAILED = 0x00000009;
    /** CONSTANT: Return value - */
  public static final int TCK_API_AUTHENTICATION_NOT_POSSIBLE = 0x0000000a;
    /** CONSTANT: Return value - */
  public static final int TCK_API_INTERNAL_ERROR=0x00000100;

  /*
   * variables
   * =========
   */
  /** Debug flag */
  private boolean isDebug = false;
  /** Indicating if the library is loaded in the environment or not. */
  private static boolean libLoaded = false;
  /** Indicating if the library is initiliazed in the environment or not. */
  private static boolean libInitialized= false;
  /** Native instance handle. */
  private static long hInstance = 0;
  /** Native resource handle. */
  private long hResId = 1;

  /**
   * Constructor.
   *
   * @exception java.lang.Exception
   */
  public GATicket() throws java.lang.Exception {
    debugMsg("GATicket", "GATicketApi v1.1 build 2004-02-01");
    GATicket.loadLib();
    try{
      debugMsg("GATicket", "call native init method");
      if (!libInitialized)
      { 
        GATicket.initLib(this.initialize(hResId), this);
      }
    }catch(Throwable err)
    {
      throw new Exception("Failed to initialize GATicket." + err.getMessage());
    }
  }

  /**
   * Loads the native library if needed.
   *
   * @exception java.lang.Exception
   */
  private static void loadLib() throws java.lang.Exception {
    try {
      if (!libLoaded) {
        System.loadLibrary(LIBRARY_NAME);   
        libLoaded = true;
      }
    }
    catch (Exception err) {
        System.out.println(" Exception: "+err);
        throw new Exception("Failed to load library: " + GATicket.LIBRARY_NAME);
    }
  }


  /**
   * Initialize
   *
   * @param instanceHandle long - the handle to the instance
   * @exception java.lang.Exception
   */
  private static void initLib(long instanceHandle, GATicket pticket) throws Exception {
    hInstance = instanceHandle;
    if (0 != hInstance){
      libInitialized = true;
    }else{
         long err = pticket.getLastError();
    	 throw new Exception("\r\nFailed to initialize TICKET_API error=" + Long.toHexString(err) + "[" + pticket.getErrorDescription(err) + "]");
    	}
  }

  /**
   * Prints a debug message to the standard out (console)
   *
   * @param method java.lang.String - name of the method
   * @param message java.lang.String - the debug message
   */
  private void debugMsg(String method, String message) {
    if (isDebug)
      debugOut("GATicketApi -> " + method + ":" + message);
  }

  /**
   * Prints a debug message to the standard out (console)
   *
   * @param message java.lang.String - the debug message
   */
  private static void debugOut(String message) {
    System.out.println(message);
  }

  /**
   * Sets the debug message on/off
   *
   * @param isDebug_v boolean - on/off
   */
  public void setIsDebug(boolean isDebug_v) {
    isDebug = isDebug_v;
  }

  /**
   * Gets library version.
   *
   * @return long - version
   * @param iWhichVersion int - 0=API, 1=GAengine
   */
  public long getGAVersion(int iWhichVersion) {
    try {
      return getGAVersion(hInstance, iWhichVersion);
    }
    catch (Throwable unknownError) {
      return 0;
    }
  }

  /**
   * Finalizes the class and returns all resources.
   */
  public void finalize() {
    try {
      finalize(hInstance);
    }
    catch (Throwable err) {
      debugMsg("finalize", err.toString()); 
    }
  }

  /**
   * Get current ticket - if none available start authentication.
   *
   * @return java.lang.String - the ticket string
   */
  public String getTicket() {
    try {
      return getTicketBuf(hInstance);
    }
    catch (Throwable err) {
      debugMsg("getTicket", err.toString());
      return null;
    }

  }

  /**
   * Gets a new ticket. -Forces (re)authentication.
   *
   * @return java.lang.String - the ticket string
   */
  public String getNewTicket() {
    try {
      return getNewTicketBuf(hInstance);
    }
    catch (Throwable err) {
      debugMsg("getNewTicket", err.toString());
      return null;
    }
  }

  /**
   * Destroy the current ticket.
   * Logs off the user - same effect as card remove.
   */
  public void destroyTicket() {
    try {
      destroyTicket(hInstance);
    }
    catch (Throwable ignore) {}
  }

  /**
   * Gets last error code.
   *
   * @return long - last error code
   */
  public long getLastError() {
    try {
      return getLastError(hInstance);
    }
    catch (Throwable unknownError) {
      return TCK_API_ERR_INTERNAL;
    }
  }

    /**
     * Gets error code description.
     *
     * @return string - error code description
     * @param Error long - error code
     */
    public String getErrorDescription(long Error) {
      try {
        return getErrorDescription(hInstance, Error);
      }
      catch (Throwable unknownError) {
        return "";
      }
    }

  /*
   * native methods
   * ==============
   */

  /**
   * Get the version of the native library.
   *
   * @return long - last error code
   */
  private native long getDllVersion();

   /**
    * Get the version of the native GATicket API/engine.
    *
    * @return long - version
    * @param hInst long - handle to instance
    * @param iWhat int  - which version is asking for (0=API, 1=GATICKET engine)
    */
  private native long getGAVersion(long hInst, int iWhat);

  /**
   * Initialize the native library.
   *
   * @return long - last error code
   * @param ResId long - resource identifier
   */
  private native long initialize(long ResId);

  /**
   * Get ticket.
   *
   * @return java.lang.String - ticket string
   * @param hInst long - handle to instance
   */
  private native String getTicketBuf(long hInst);

  /**
   * Get new ticket - forces authentication.
   *
   * @return java.lang.String - ticket string
   * @param hInst long - handle to instance
   */
  private native String getNewTicketBuf(long hInst);

  /**
   * Destroys current tickets if present.
   *
   * @return int - code
   * @param hInst long - handle to instance
   */
  private native int destroyTicket(long hInst);

  /**
   * Gets the last error code
   *
   * @return long - error code
   * @param hInst long - handle to instance
   */
  private native long getLastError(long hInst);

  /**
   * Get en description to an given error code.
   *
   * @return java.lang.String - description string
   * @param hInst long - handle to instance
   * @param Error long - error code
   */
  private native String getErrorDescription(long hInst, long Error);

  /**
   * Finalize and free up resources.
   *
   * @param hInst long - handle to instance
   */
  private native void finalize(long hInst);

  /**
   * Test the application <B>JUST FOR TESTING -REMOVE</B><H1>REMOVE B4 RELEASE !!!!!!!!</H1>
   */

}