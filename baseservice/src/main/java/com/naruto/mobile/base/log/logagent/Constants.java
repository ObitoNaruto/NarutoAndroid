package com.naruto.mobile.base.log.logagent;

/**
 * 框架常量类 包含大量埋点常量 TODO 常量类 里有很多业务viewID？
 * 
 * @author
 * 
 */
public class Constants {
	public final static int STORAGE_TYPE_APPLANCHER = 1;
	public final static int STORAGE_TYPE_PAGEJUMP = 2;
	public final static int STORAGE_TYPE_EVENT = 3;
	public final static int STORAGE_TYPE_ERROR = 4;
	public final static int STORAGE_TYPE_VIEWMODEL = 5;

	public static boolean LOG_SWITCH = true;
	public static int LOG_ACCOUNT = 0;
	public static Object lock = new Object();
	public final static String LOGFILE_PATH = "/logs";
	public final static String LOGFILE_NAME = "/userlog.log";

	public final static int LOG_MAX_ACCOUNT = 20;
	public static long LAST_SEND_TIME = 0;

	public final static String BARCODEVIEW = "barcodeView";

//	public enum ExceptionType {
//		TYPE_CONNECTERR("MonitorPoint_ConnectErr"), TYPE_EXCEPTION(
//				"MonitorPoint_Exception");
//
//		private String type;
//
//		private ExceptionType(String type) {
//			this.type = type;
//		}
//
//		public String getType() {
//			return type;
//		}
//	}

	// memo
	public static final String STATE_LOGIN = "Y";
	public static final String STATE_UNLOGIN = "N";
	public static final String ONEPERSONSHARE = "onePersonShare";
	public static final String ANYPERSONSHARE = "anyPersonShare";

	public static final String MONITORPOINT_CLIENTSERR = "MonitorPoint_ClientsErr";
	public static final String MONITORPOINT_CONNECTERR = "MonitorPoint_ConnectErr";
	public static final String MONITORPOINT_EXCEPTION = "MonitorPoint_Exception";
	public static final String MONITORPOINT_EVENT_VIEWRETURN = "MonitorPoint_viewReturn";
	public static final String MONITORPOINT_EVENT_BIZRESULT = "MonitorPoint_BizResult";
	public static final String MONITORPOINT_EVENT_CHECKUPDATE = "MonitorPoint_CheckUpdate";
	public static final String MONITORPOINT_EVENT_BUTTONCLICKED = "MonitorPoint_ButtonClicked";
	public static final String MONITORPOINT_EVENT_SHAREINFO = "MonitorPoint_ShareInfo";
	public static final String MONITORPOINT_EVENT_CONTACT = "MonitorPoint_ContactFrom";
	public static final String MONITORPOINT_EVENT = "MonitorPoint_Event";

	// sub event id
	public static final String EVENTTYPE_PAIPAISCANRESOULT = "eventType_PaipaiScanResoult";
	public static final String EVENTTYPE_GOTONEWTRANSFERPAGE = "eventType_gotoNewTransferPage";
	public static final String EVENTTYPE_SUPERTRANSFERREADYSHAKE = "eventType_superTransferReadyShake";
	public static final String EVENTTYPE_SHAKESUCCESSUSEDTIME = "eventType_ShakeSuccessUsedTime";
	public static final String EVENTTYPE_CONFIRMTRANSFERINFO_ALIPAYACCOUNT = "eventType_confirmTransferInfo_alipayAccount";
	public static final String EVENTTYPE_CONFIRMTRANSFERINFO_MOBILENO = "eventType_confirmTransferInfo_mobileNo";
	public static final String EVENTTYPE_CONFIRMTRANSFERACCONTBUTTONCLICK = "eventType_confirmTransferAccontButtonClick";
	public static final String EVENTTYPE_HISTORYCONTACTBUTTONCLICK = "eventType_historyContactButtonClick";
	public static final String EVENTTYPE_LOCALCONTACTBUTTONCLICK = "eventType_localContactButtonClick";
	public static final String EVENTTYPE_TRANSFERCALL = "eventType_transferCall";
	public static final String EVENTTYPE_GESTURESETTINGSUCCESS = "eventType_KB_GestureSetting_success";
	public static final String EVENTTYPE_TICKETZONECLICKED = "eventType_KB_TicketZoneClicked";
	public static final String EVENTTYPE_BILLZONECLICKED = "eventType_KB_BillZoneClicked";
	public static final String EVENTTYPE_MYTICKETCLICKED = "eventType_KB_MyTicketClicked";
	public static final String EVENTTYPE_UPDATECLICKED = "eventType_Update";
	public static final String EVENTTYPE_KB_TICKETDETAILSOUNDBUTTONCLICKED = "eventType_KB_TicketDetailSoundButtonClicked";
	public static final String EVENTTYPE_KB_TICKETDETAILREADDETAILBUTTONCLICKED = "eventType_KB_TicketDetailReadDetailButtonClicked";
	public static final String EVENTTYPE_SENDSMSBUTTONCLICKINCREDITCARDVIEW = "eventType_SendSmsButtonClickInCreditCardView";
	public static final String EVENTTYPE_CALLBUTTONCLICKINCREDITCARDVIEW = "eventType_CallButtonClickInCreditCardView";

	// current view info
	public final static String STORAGE_RECORDER = "recorder";
	public final static String STORAGE_TCID = "TCID";
	public final static String STORAGE_CURRENTVIEWID = "currentViewID";
	public final static String STORAGE_APPID = "appID";
	public final static String STORAGE_APPVERSION = "appVersion";
	public final static String STORAGE_PRODUCTID = "productID";
	public final static String STORAGE_PRODUCTVERSION = "productVersion";
	public final static String STORAGE_CLIENTID = "clientID";
	public final static String STORAGE_ALIPAYID = "alipayID";
	public final static String STORAGE_UUID = "uuID";
	public final static String STORAGE_USERID = "userID";
	public final static String STORAGE_MODELVERSION = "modelVersion";
	public final static String STORAGE_REQUESTTYPE = "requestType";

	// button id
	public final static String LOGINBUTTON = "LoginButton";
	public final static String CONTACT_ALIPAY = "contact_alipay";
	public final static String CONTACT_PHONE = "contact_phone";
	public final static String CONTACT_BARCODE = "contact_barcode";

	// viewId need record
	public final static String TRADERECORDSVIEW = "tradeRecordsView";
	public final static String TRADERECORDSWAITVIEW = "tradeRecordsWaitView";
	public final static String TRADERECORDSFORALLVIEW = "tradeRecordsAllView";
	public final static String TRADEDETAILSVIEW = "tradeDetailsView";
	public final static String TRADEMSGVIEW = "tradeMsgView";
	public final static String WITHDRAWVIEW = "withdrawView";
	public final static String FEEDBACKVIEW = "feedbackView";
	public final static String NOTIFYSETTINGVIEW = "notifySettingView";
	public final static String SAFEPAYSETTINGVIEW = "safePaySettingView";
	public final static String LOGINVIEW = "loginView";
	public final static String HELPVIEW = "helpView";
	public final static String CARDMANAGEVIEW = "cardManageView";
	public final static String PROTOCOLVIEW = "protocolView";
	public final static String SUBSTITUTEPAYHOMEVIEW = "substitutePayHomeView";
	public final static String ONEPERSONSUBSTITUTEPAYVIEW = "onePersonsubstitutePayView";
	public final static String ANYBODYSUBSTITUTEPAYVIEW = "anyBodysubstitutePayView";
	public final static String ANYBODYSUBSTITUTEPAYSELECTTYPEVIEW = "AnyBodysubstitutePaySelectTypeView";
	public final static String HOMEVIEW = "homeView";
	public final static String FINDLOGINPASSWORDVIEW = "findLoginPasswordView";
	public final static String ACCOUNTMANAGEVIEW = "accountManageView";
	public final static String REGISTERVIEW = "registerView";
	public final static String GETREGISTERSMSVIEW = "getRegisterSmsView";
	public final static String SELECTACCOUNTVIEW = "selectAccountView";
	public final static String SCANBARCODEVIEW = "scanBarCodeView";
	public final static String CASHREGISTERVIEW = "cashRegisterView";
	public final static String PHONEBINDINGVIEW = "phoneBindingView";
	public final static String BINDINGCHECKCODEVIEW = "bindingCheckCodeView";
	public final static String MOREVIEW = "moreView";
	public final static String RECOMMANDVIEW = "recommandView";
	public final static String WALLETACCOUNT = "walletAccount";
	public final static String WALLETBILL = "walletBill";
	public final static String WALLETTICKET = "walletTicket";

	public final static String VIEWID_GestureView = "gestureView";
	public final static String VIEWID_NoneView = "-";
	public final static String VIEWID_PasswordView = "passwordView";
	public final static String VIEWID_SetGestureView = "setGestureView";
	public final static String VIEWID_AlipayLoginView = "alipayLoginView";
	public final static String VIEWID_TaobaoLoginView = "taobaoLoginView";
	public final static String VIEWID_BankCardDetails = "bankCardDetails";
	public final static String VIEWID_WithdrawHome = "withdrawHome";
	public final static String VIEWID_ModifyBankPhoneHome = "modifyBankPhoneHome";
	public final static String VIEWID_SeeLimitHome = "seeLimitHome";
	public final static String VIEWID_RepaymentHome = "repaymentHome";
	public final static String VIEWID_SignBankCardHome = "signBankCardHome";
	public final static String VIEWID_InputCardView = "inputCardView";
	public final static String VIEWID_BankCardList = "bankCardList";
	public final static String VIEWID_SmsConfirmView = "smsConfirmView";
	public final static String VIEWID_SignResultView = "signResultView";
	public final static String VIEWID_ManagePasswordView = "managePasswordView";
	public final static String VIEWID_PwdMngHome = "pwdMngHome";
	public final static String VIEWID_InputLoginPwdView = "inputLoginPwdView";
	public final static String VIEWID_CardListView = "cardListView";
	public final static String VIEWID_CardInputView = "cardInputView";
	public final static String VIEWID_BankListView = "bankListView";
	public final static String VIEWID_TransferToCardConfirmView = "transferToCardConfirmView";

	public final static String APPID_signBankCard = "signBankCard";
	public final static String APPID_walletBankCard = "walletBankCard";
	// 应用id 首页
	public final static String WALLETAPPSHOW = "walletAppShow";
	public final static String APPID_CCR = "09999999"; // 信用卡还款

	// view id
	public final static String VIEWID_CCR_NEW = "newCardFromView"; // 新卡还款界面
	public final static String VIEWID_CCR_OLD = "oldCardFromView"; // 老卡还款界面
	public final static String VIEWID_CCR_SUCCESS = "repaymentSuccessView"; // 还款成功界面
	public final static String WALLETHOME = "walletHome";
	public final static String ACCOUNTHOME = "accountHome";
	public final static String MSGLIST = "msgList";
	public final static String BANKCARDLIST = "bankCardList";
	public final static String FACEHOME = "faceHome";
	public final static String BILLLIST = "billList";
	public final static String MYTICKETLIST = "myTicketList";
	public final static String MYNAMECARD = "myNameCard";
	public final static String SAVEFACEVIEW = "saveFaceView";
	public final static String SEEFACEVIEW = "seeFaceView";

	public final static String APP1IDHOME = "app1IdHome";
	public final static String APP2IDHOME = "app2IdHome";
	public final static String APP3IDHOME = "app3IdHome";
	public final static String APP4IDHOME = "app4IdHome";
	public final static String HOMEAPPSHOW = "homeAppShow";
	public final static String APPCENTER = "appCenter";

	// 头像埋点id
	public final static String SETFACEICON = "setFaceIcon";
	public final static String BACKICON = "backIcon";
	public final static String SAVEICON = "saveIcon";
	public final static String EDITICON = "editIcon";
	// 埋点id

	// 通用seed id
	public final static String SEEDID_NEXTBUTTON = "nextButton"; // 下一步按钮
	public final static String SEEDID_INPUTBOX = "inputBox"; // 输入框seed
	public final static String SEEDID_BACKICON = "backIcon"; // 返回
	public final static String SEEDID_CONFIRM_BUTTON = "confirmButton"; // 确定
	public final static String SEEDID_RADIO_BUTTON = "radioButton"; // 复选框
	public final static String SEEDID_AGREEMENT = "agreement"; // 点击阅读协议链接

	// 杂 seedid
	public final static String SEEDID_PHONEQUERY = "phoneQuery";
	public final static String SEEDID_SMSQUERY = "smsQuery";
	public final static String SEEDID_EXPERIENCEICON = "experienceIcon";
	public final static String SEEDID_BILLQUERY = "billQuery";
	public final static String SEEDID_REMINDBUTTON = "remindButton";
	public final static String SEEDID_CLICKOLDCARD = "clickOldCard";
	public final static String SEEDID_CREATICON = "creatIcon";
	public final static String ACCOUNTHOMEZONE = "accountHomeZone";
	public final static String MSGICON = "msgIcon";
	public final static String BANKCARDICON = "bankCardIcon";
	public final static String FACEBOOKICON = "faceBookIcon";
	public final static String BILLZONE = "billZone";
	public final static String TICKETZONE = "ticketZone";

	public final static String HOMEAPP1ICON = "homeApp1Icon";
	public final static String HOMEAPP2ICON = "homeApp2Icon";
	public final static String HOMEAPP3ICON = "homeApp3Icon";
	public final static String HOMEAPP4ICON = "homeApp4Icon";

	public final static String HOMEAPPSHOWICON = "homeAppShowIcon";

	public final static String Seed_taobao = "taobao";
	public final static String Seed_alipay = "alipay";
	public final static String Seed_LaunchAlipayWallet = "launchAlipayWallet";
	public final static String Seed_CheckGesture = "checkGesture";
	public final static String Seed_SetGestureButton = "setGestureButton";
	public final static String Seed_SkipGestureButton = "skipGestureButton";
	public final static String Seed_SeeBankCard = "seeBankCard";
	public final static String Seed_AddBankCardIcon = "addBankCardIcon";
	public final static String Seed_WithdrawIcon = "withdrawIcon";
	public final static String Seed_ModifyBankPhoneIcon = "modifyBankPhoneIcon";
	public final static String Seed_SeeLimitIcon = "seeLimitIcon";
	public final static String Seed_RepaymentIcon = "repaymentIcon";
	public final static String Seed_SignBankCardIcon = "signBankCardIcon";
	public final static String Seed_DelBankCardIcon = "delBankCardIcon";
	public final static String Seed_BackIcon = "backIcon";
	public final static String Seed_ConfirmButton = "confirmButton";
	public final static String Seed_NextButton = "nextButton";
	public final static String Seed_PwdMng = "pwdMng";
	public final static String Seed_SetGesture = "setGesture";
	public final static String Seed_PhoneBookIcon = "phoneBookIcon";
	public final static String Seed_seeRecent = "seeRecent";
	public final static String Seed_seePhoneBook = "seePhoneBook";
	public final static String Seed_searchInput = "searchInput";
	public final static String Seed_closeIcon = "closeIcon";
	public final static String Seed_helpIcon = "helpIcon";
	public final static String Seed_transferToWhoInput = "transferToWhoInput";
	public final static String Seed_confirm = "confirm";
	public final static String Seed_transferByPhone = "transferByPhone";
	public final static String Seed_transferToCard = "transferToCard";
	public final static String Seed_transferToAccount = "transferToAccount";
	public final static String Seed_accountIcon = "accountIcon";
	public final static String Seed_selectAccount = "selectAccount";
	public final static String Seed_smsSwitch = "smsSwitch";
	public final static String Seed_addAccountSwitch = "addAccountSwitch";
	public final static String Seed_accountInput = "accountInput";

	public final static String Seed_CardIcon = "cardIcon";
	public final static String Seed_CardInput = "cardInput";
	public final static String Seed_BankIcon = "bankIcon";
	public final static String Seed_CancelButton = "cancelButton";
	public final static String Seed_SelectCard = "selectCard";
	public final static String Seed_CloseIcon = "closeIcon";
	public final static String Seed_SearchInput = "searchInput";
	// 公共缴费单viewId
	public final static String WATERINPUTVIEW = "waterInputView";
	public final static String ELECTRICITYINPUTVIEW = "electricityInputView";
	public final static String GASINPUTVIEW = "gasInputView";
	public final static String WIDELINEINPUTVIEW = "widelineInputView";
	public final static String LOTTERYVIEW = "lotteryView";

	// 信用卡还款ViewId
	public final static String CCRNEWUSERVIEW = "CCRNewUserView";
	public final static String CCROLDUSERVIEW = "CCROldUserView";
	public final static String CCRPAYOKVIEW = "CCRPayOKView";
	public final static String CCRKNOWNVIEW = "CCRKnownView";

	// 卡宝我的券埋点
	public final static String KABAOMYTICKETLISTVIEW = "kaBaoMyTicketListView"; // 列表
	public final static String KKABAOTICKETSTOREVIEW = "kaBaoTicketStoreView"; // 市场
	public final static String KABAOTICKETDETAILVIEW = "kaBaoTicketDetailView"; // 详情

	// 卡包银行卡列表埋点
	public static final String WALLE_BANK_CARD = "walletBankCard";
	public static final String ADD_BANK_CARD = "addBankCard";
	public static final String BANK_CARD_LIST = "bankCardList";
	public static final String ADD_CARD_BANK_ICON = "addCardBankIcon";
	public static final String BANK_CARD_DETAILS = "bankCardDetails";
	public static final String SEE_BANK_CARD = "seeBankCard";
	public static final String BACK_ICON = "backIcon";

	// 转账埋点
	public static final String TRANSFROMVIEW = "transferByPhoneView";
	public static final String TRANSFERCONFIRMVIEW = "transferConfirmView";
	public static final String PHONEBOOKVIEW = "phoneBookView";
	public static final String TRANSFERHELPVIEW = "transferHelpView";
	public static final String TRANSFERTOWHOVIEW = "transferToWhoView";
	public static final String TRANSFERENTRANCEVIEW = "09999988Home";
	public static final String TRANSFERTOCARDVIEW = "transferToCardView";
	public static final String TRANSFERTOACCOUNTVIEW = "transferToAccountView";
	public static final String ACCOUNTLISTVIEW = "accountListView";
	public static final String TRANSFERTOACCOUNTCONFIRMVIEW = "transferToAccountConfirmView";
	public static final String TRANSFERBYPHONEVIEW = "transferByPhoneView";

	public static final String APPID_TRANSFER = "09999988";
	// 悦享拍埋点
	public static boolean firstOpenCM;
	public static long paipaiStep1Start;
	public static long paipaiStep1End;

	public static boolean parserQR;
	public static long paipaiStep3Start;
	public static long paipaiStep3End;

	// 当面付埋点
	public final static String APPID_QUICKPAY = "09999989";
	public final static String QUICKPAY_PAYFIRSTVIEW = "payFirstView";
	public final static String QUICKPAY_PAYSEARCHVIEW = "paySearchView";
	public final static String QUICKPAY_PAYCONFIRMVIEW = "payConfirmView";

	public final static String QUICKPAY_PAYBUTTON = "payButton";
	public final static String QUICKPAY_RESEARCHBUTTON = "reSearchButton";
	public final static String QUICKPAY_CONFIRMPAYBUTTON = "confirmPayButton";
	public final static String QUICKPAY_REFRESHBUTTON = "reFreshButton";

	public final static String CANCELBUTTON = "cancelButton";
	public final static String CONFIRMBUTTON = "confirmButton";

	public final static String APPLYID = "applyid";
	public final static String SENDWAVE = "sendwave";
	public final static String RECEIVEWAVE = "receivewave";
	public final static String ORDERCREATE = "ordercreate";
	public final static String ORDERPAY = "orderpay";
	public final static String PUSHORDER = "pushorder";
	public final static String LOOPORDER = "looporder";
	public final static String VERIFYWAVE = "verifywave";

	public final static String CONFIRMPAY = "confirmpay";
	public final static String CALLCASHIER = "callcashier";
	// 安全相关埋点
	public final static String APPID_SECURITY = "security";
	// viewID
	public final static String SECURITY_VIEWID_CHOOSEACCOUNTVIEW = "chooseAccountView";
	public final static String SECURITY_VIEWID_ALIPAYLOGINVIEW = "alipayLoginView";
	public final static String SECURITY_VIEWID_TAOBAOLOGINVIEW = "taobaoLoginView";
	public final static String SECURITY_VIEWID_SETGESTUREVIEW = "setGestureView";
	public final static String SECURITY_VIEWID_CHECKGESTUREVIEW = "checkGestureView";
	public final static String SECURITY_VIEWID_PWDMANAGEVIEW = "pwdManageView";
	public final static String SECURITY_VIEWID_SECURITYHOME = "securityHome";
	public final static String SECURITY_VIEWID_SECURITYLEVELVIEW = "securityLevelView";
	public final static String SECURITY_VIEWID_ALIPAYBINDINGVIEW = "alipayBindingView";
	public final static String SECURITY_VIEWID_SECURITYCHECKUPINDEX = "securityCheckUpIndex"; // 安全体检
	public final static String SECURITY_VIEWID_BINDINGMOBILEVIEW = "bindingMobileView";
	public final static String SECURITY_VIEWID_REALNAMECHECKVIEW = "realNameCheckView";
	public final static String SECURITY_VIEWID_BINDINGMOBILEBOX = "bindingMobileBox";
	public final static String SECURITY_VIEWID_CHECKDEVICEVIEW = "checkDeviceView";

	// 埋点ID
	public final static String SECURITY_MONITORID_USEOLDACCOUNT = "useOldAccount";
	public final static String SECURITY_MONITORID_USENEWACCOUNT = "useNewAccount";
	public final static String SECURITY_MONITORID_REGISTERBUTTON = "registerButton";
	public final static String SECURITY_MONITORID_LOGINBUTTON = "loginButton";
	public final static String SECURITY_MONITORID_FORGETPASSWORD = "forgetPassword";
	public final static String SECURITY_MONITORID_SKIPBUTTON = "skipButton";
	public final static String SECURITY_MONITORID_SETGESTURE = "setGesture";
	public final static String SECURITY_MONITORID_CHECKGESTURE = "checkGesture";
	public final static String SECURITY_MONITORID_FORGETGESTURE = "forgetGesture";
	public final static String SECURITY_MONITORID_OPENGESTURE = "openGesture";
	public final static String SECURITY_MONITORID_CLOSEGESTURE = "closeGesture";
	public final static String SECURITY_MONITORID_MODIFYPAYPWD = "modifyPayPwd";
	public final static String SECURITY_MONITORID_FOUNDPAYPWD = "foundPayPwd";
	public final static String SECURITY_MONITORID_MODIFYLOGINPWD = "modifyLoginPwd";
	public final static String SECURITY_MONITORID_SECURITYQUITBUTTON = "securityQuitButton";
	public final static String SECURITY_MONITORID_SECURITYLEVEL = "securityLevel";
	public final static String SECURITY_MONITORID_LOGINLOG = "loginLog";
	public final static String SECURITY_MONITORID_MESSAGENOTIFY = "messageNotify";
	public final static String SECURITY_MONITORID_ACCOUNTMANAGE = "accountManage";
	public final static String SECURITY_MONITORID_MOBILEBAOLING = "mobileBaoLing";
	public final static String SECURITY_MONITORID_SMALLDENSEFREE = "smallDenseFree";
	public final static String SECURITY_MONITORID_PWDMANAGE = "pwdManage";
	public final static String SECURITY_MONITORID_XIAOBAOHELP = "xiaoBaoHelp";
	public final static String SECURITY_MONITORID_CUSTOMERSERVICE = "customerService";
	public final static String SECURITY_MONITORID_ABOUT = "about";
	public final static String SECURITY_MONITORID_BINDINGMOBILE = "bindingMobile";
	public final static String SECURITY_MONITORID_OPENBAOLING = "openBaoLing";
	public final static String SECURITY_MONITORID_CONFIRMBINDING = "confirmBinding";

	// 账单
	public final static String APPID_BILL = "bill";

	public final static String VIEWID_MYBILLLIST = "myBillList";
	public final static String VIEWID_BILLFILTERVIEW = "billFilterView";
	public final static String VIEWID_BALANCELIST = "balanceList";
	public final static String VIEWID_BALANCEFILTERVIEW = "balanceFilterView";
	public final static String VIEWID_MYBILLDETAILS = "myBillDetails";
	public final static String VIEWID_CONTACTVIEW = "contactView";
	public final static String VIEWID_TRANSFERTOACCVIEW = "transferToAccountView";
	public final static String VIEWID_TRANSFERTOCARDVIEW = "transferToCardView";
	public final static String VIEWID_TRANSFERTOPHONEVIEW = "transferByPhoneView";
	public final static String VIEWID_PAYFEESVIEW = "PayFeesView";
	public final static String VIEWID_OLDCARDVIEW = "oldCardFromView";
	public final static String VIEWID_PHONEINPUTVIEW = "phoneInputView";

	public final static String SEED_BILLFILTERICON = "billFilterIcon";
	public final static String SEED_ALLBILL = "allBill";
	public final static String SEED_SHOPPING = "shopping";
	public final static String SEED_TRANSFER = "transfer";
	public final static String SEED_CARDPAY = "cardPay";
	public final static String SEED_LIFEPAY = "lifePay";
	public final static String SEED_MONTHBALANCE = "monthBalance";
	public final static String SEED_BALANCEICON = "balanceIcon";
	public final static String SEED_BALANCEFILTERICON = "balanceFilterIcon";
	public final static String SEED_ALLBALANCE = "allBalance";
	public final static String SEED_INCOME = "income";
	public final static String SEED_EXPENSE = "expense";
	public final static String SEED_SEEBILL = "seeBill";
	public final static String SEED_HEAD = "head";
	public final static String SEED_RECHARGEBUTTON = "rechargeButton";
	public final static String SEED_TRANSFERACCOUNTBUTTON = "transferToAccountButton";
	public final static String SEED_TRANSFERCARDBUTTON = "transferToCardButton";
	public final static String SEED_TRANSFERPHONEBUTTON = "transferByPhoneButton";
	public final static String SEED_PAYFEESBUTTON = "payFeesButton";
	public final static String SEED_PAYCARDBUTTON = "payCardButton";
	public final static String SEED_PAYBUTTON = "PayButton";
	public final static String SEED_CONFIRMRECEIPTBUTTON = "confirmReceiptButton";
	public final static String SEED_CHANGEBUTTON = "changeButton";
	public final static String SEED_REFUSEBUTTON = "refuseButton";
	public final static String SEED_PAYFORANOTHERBUTTON = "payForAnotherButton";
	public final static String SEED_DELBILL = "delBill";
	public final static String SEED_DELSUCCESS = "delSuccess";

	// 资产埋点表
	public final static String LOG_APPID_ASSET_DEFAULT = "assets"; // 少数无app
																	// id的埋点需求使用该appid
	// 资产view id
	public final static String VIEWID_MY_ASSETS = "myAssets"; // 我的资产
	public final static String VIEWID_MY_COUPON = "myCouponList"; // 我的卡券
	public final static String VIEWID_COUPON_APP = "couponApp"; // 卡券外部应用id
	public final static String VIEWID_MY_CARD = "myCard"; // 我的银行卡
	public final static String VIEWID_MY_PHONE = "myPhone"; // 我的手机
	public final static String VIEWID_MY_Travel = "myTravelList"; // 我的旅行
	public final static String VIEWID_BALANCEBAO = "balanceBaoIndex"; // 与我的宝令首页保持一直
	public final static String VIEWID_MY_CARD_DETAILS = "myCardDetails"; // 我的银行卡详情
	public final static String VIEWID_OPEN_QUICK_PAY = "openQuickPay"; // 开通快捷
	public final static String VIEWID_OPEN_QUICK_PAY_DETAILS = "openQuickPayDetails"; // 开通快捷详情
	public final static String VIEWID_OPEN_QUICK_PAY_VERIFY = "openQuickPayVerify"; // 开通快捷短信校验
	public final static String VIEWID_ACCOUNT_DETAILS = "accountDetails"; // 账户详情
	public final static String VIEWID_WITHDRAW = "withdraw";
	public final static String VIEWID_BALANCE_BAO_GUIDE1 = "balanceBaoGuide1"; // 余额宝引导1
	public final static String VIEWID_BALANCE_BAO_GUIDE2 = "balanceBaoGuide2"; // 余额宝引导2
	public final static String VIEWID_BALANCE_BAO_GUIDE3 = "balanceBaoGuide3"; // 余额宝引导3
	public final static String VIEWID_BALANCE_BAO_CONFIRM = "balanceBaoConfirm"; // 余额宝信息确认
	public final static String VIEWID_BALANCE_BAO_BUY = "balanceBaoBuy"; // 余额宝买入信息
	public final static String VIEWID_BALANCE_BAO_BUY_SUCCESS = "balanceBaoBuySuccess"; // 余额宝买入成功
	public final static String VIEWID_BALANCE_BAO_INDEX = "balanceBaoIndex"; // 余额宝首页
	public final static String VIEWID_BALANCE_BAO_TRADE = "balanceBaoTrade"; // 余额宝交易详情
	public final static String VIEWID_BALANCE_BAO_SELL = "balanceBaoSell"; // 余额宝转出
	public final static String VIEWID_OPEN_CREDIT_PAY_VIEW = "openCreditPayView"; // 开通信任宝页
	public final static String VIEWID_CREDIT_PAY_INDEX = "creditPayIndex"; // 信任宝首页
	public final static String VIEWID_OPEN_CREDIT_PAY_CHECK_FAIL_VIEW = "openCreditPayCheckFailView"; // 开通信任宝资质认证失败页
	public final static String VIEWID_OPEN_CREDIT_PAY_CONFIRM_VIEW = "openCreditPayConfirmView"; // 开通信任宝确认页
	public final static String VIEWID_OPEN_CREDIT_PAY_SMS_CONFIRM_VIEW = "openCreditPaySmsConfirmView"; // 输入短信校验码页
	public final static String VIEWID_OPEN_CREDIT_PAY_SUCCESS_VIEW = "openCreditPaySuccessView"; // 开通信任宝成功页
	public final static String VIEWID_OPEN_CREDIT_PAY_FAIL_VIEW = "openCreditPayFailView"; // 开通信任宝失败页
	public final static String VIEWID_OPEN_CREDIT_PAY_BILL_DETAILS = "creditPayBillDetails"; // 账单明细页
	public final static String VIEWID_OPEN_CREDIT_PAY_BILL_LIST = "creditPayBillList"; // 信任宝账单页
	public final static String VIEWID_OPEN_CREDIT_PAY_CONFIRM_REPAYMENT_VIEW = "creditPayConfirmRepaymentView"; // 信任宝确认还款页
	// 余额宝二期 2013-08-01
	public final static String VIEWID_AUTO_BUY_SWITCH = "autoBuySwitch"; // 自动转入开关页
	public final static String VIEWID_BOUND_PHONE_CAN_OPEN = "boundPhoneCanOpen";
	public final static String VIEWID_SELECT_CARD_VIEW = "selectCardView";
	public final static String VIEWID_BALANCE_BAO_SELL_TO_CARD = "balanceBaoSellToCard";
	public final static String VIEWID_SELECT_TO_CARD_TIME = "selectToCardTime";
	public final static String VIEWID_SUCCESS_TO_CARD = "successToCard";
	public final static String VIEWID_BALANCE_BAO_SELL_TO_ACCOUNT = "balanceBaoSellToAccount";
	public final static String VIEWID_BALANCE_BAO_SHARE = "balanceBaoShare";
	public final static String VIEWID_NO_GAINS_TO_YAO = "noGainsToYao";
	public final static String VIEWID_TO_YAO_INDEX = "toYaoIndex";
	public final static String VIEWID_XIAO_BAO_HELP = "xiaoBaoHelp";
	public final static String VIEWID_BALANCE_BAO_TRADE_INCOME = "balanceBaoTradeIncome";
	public final static String VIEWID_BALANCE_BAO_TRADE_BUY = "balanceBaoTradeBuy";
	public final static String VIEWID_BALANCE_BAO_TRADE_SELL = "balanceBaoTradeSell";
	public final static String VIEWID_BALANCE_BAO_TRADE_CONSUMPTION = "balanceBaoTradeConsumption";

	// 资产seed id

	// 资产-我的账户详情 seed
	public final static String SEEDID_ACCOUNT_DETAIL_MY_ASSET = "myAssets"; // 返回资产页(这里本应该是backIcon,但现为兼容ios的seed)
	public final static String SEEDID_ACCOUNT_DETAIL_CHANGE_AVATAR = "changeYourPhoto"; // 修改头像
	public final static String SEEDID_ACCOUNT_DETAIL_RECHARGE = "recharge"; // 充值
	public final static String SEEDID_ACCOUNT_DETAIL_WITHDRAW = "withdraw"; // 提现
	public final static String SEEDID_ACCOUNT_DETAIL_COUPON = "hongbao"; // 红包
	public final static String SEEDID_ACCOUNT_DETAIL_JFB = "jifenbao"; // 集分宝

	// 资产-我的账户详情 seed
	public final static String SEEDID_MYCOUPON = "myCoupon"; // 我的卡券
	public final static String SEEDID_MYACCOUNT = "myAccount"; // 我的账户

	public final static String SEEDID_BALANCEBAO = "balanceBao"; // 余额宝
	public final static String SEEDID_MYCARD = "myCard"; // 我的银行卡
	public final static String SEEDID_MYPHONE = "myPhone"; // 我的手机
	public final static String SEEDID_MYASSETS = "myAssets"; // 我的资产
	public final static String SEEDID_MYTRAVEL = "myTravel"; // 我的旅行
	public final static String SEEDID_ADDCARD = "addCard"; // 添加银行卡
	public final static String SEEDID_SEECARD = "seeCard"; // 银行卡详情
	public final static String SEEDID_QUERYCARDBALANCE = "queryCardBalance"; // 查询银行卡余额
	public final static String SEEDID_WITHDRAW = "withdraw"; // 提现
	public final static String SEEDID_BANKSERVICENETWORK = "bankServiceNetwork"; // 银行服务网店
	public final static String SEEDID_BANKSERVICETEL = "bankServiceTel"; // 银行电话
	public final static String SEEDID_BANKSERVICESMS = "bankServiceSMS"; // 银行短信
	public final static String SEEDID_PAYMENT = "payment"; // 还款
	public final static String SEEDID_OPENPAYMENTREMIND = "openPaymentRemind"; // 打开还款提醒
	public final static String SEEDID_CLOSEPAYMENTREMIND = "closePaymentRemind"; // 关闭还款提醒

	// 资产-我的手机 seed
	public final static String SEEDID_MY_PHONE_ASSET = "myAssets"; // 返回资产页(这里本应该是backIcon,但现为兼容ios的seed)
	public final static String SEEDID_MY_PHONE_RECHARGE = "recharge"; // 手机充值
	public final static String SEEDID_MY_PHONE_TEL = "serviceTel"; // 客服电话

	// 资产-余额转出(原提现)
	public final static String SEEDID_WITHDRAW_SELECT_CARD = "selectCard"; // 选择银行卡
	public final static String SEEDID_WITHDRAW_INPUT_MONEY = "inputMoney"; // 输入金额
	public final static String SEEDID_WITHDRAW_INPUT_PAY_PWD = "inputPayPwd"; // 输入支付密码
	public final static String SEEDID_WITHDRAW_SELECT_ARRIVE_DATE = "selectFinishTime"; // 选择到账时间

	// 资产-开通快捷 seed
	public final static String SEEDID_QUICK_PAY_CARD_EXPIRE_DATE = "cardExpiryDate"; // 信用卡日期选择
	public final static String SEEDID_QUICK_PAY_NAME = "name"; // 姓名输入
	public final static String SEEDID_QUICK_PAY_ID_CARD = "idCard"; // 身份证输入

	// 资产-余额宝
	public final static String SEEDID_FUND_FILTERICON = "filterIcon"; // 点击筛选
	public final static String SEEDID_FUND_BUY = "buy"; // 点击买入
	public final static String SEEDID_FUND_SELL = "sell"; // 点击转出
	public final static String SEEDID_FUND_CONSUMPTION = "consumption"; // 点击消费
	public final static String SEEDID_FUND_INCOME = "income"; // 点击收益
	public final static String SEEDID_FUND_INPUTMONEY = "inputMoney"; // 点击转出金额输入框
	public final static String SEEDID_FUND_INPUTPAYPWD = "inputPayPwd"; // 点击支付密码输入框
	public final static String SEEDID_FUND_OPEN_BUTTON = "openButton"; // 点击开通
	public final static String SEEDID_FUND_BUY_BUTTON = "buyButton"; // 点击买入
	public final static String SEEDID_FUND_SELL_BUTTON = "sellButton"; // 点击转出
	// 2013-08-01
	public final static String SEEDID_FUND_AUTO_BUY = "autoBuy";
	public final static String SEEDID_FUND_OPEN_AUTO_BUY = "openAutoBuy";
	public final static String SEEDID_FUND_CLOSE_AUTO_BUY = "closeAutoBuy";
	public final static String SEEDID_FUND_SEE_CARD = "seeCard";
	public final static String SEEDID_FUND_SELECT_CARD = "selectCard";
	public final static String SEEDID_FUND_BACK_ICON = "backIcon";
	public final static String SEEDID_FUND_FINISH_ICON = "finishIcon";
	public final static String SEEDID_FUND_SEE_TO_CARD_TIME = "seeToCardTime";
	public final static String SEEDID_FUND_CONFIRM_BUTTON = "confirmButton";
	public final static String SEEDID_FUND_TO_ACCOUNT = "toAccount";
	public final static String SEEDID_FUND_SHARE = "share";
	public final static String SEEDID_FUND_TO_YAO = "toYao";
	public final static String SEEDID_FUND_ONLINE_SERVICE = "onlineService";
	public final static String SEEDID_FUND_ADD_UP_INCOME = "addUpIncome";
	public final static String SEEDID_FUND_SEE_CURVE = "seeCurve";
	// 卡详情
	public final static String SEEDID_CARD_EMAIL_BILL_ICON = "emailBillIcon";
	public final static String SEEDID_CARD_OPEN_CARD_BILL = "openCardBill";
	public final static String SEEDID_CARD_QUERY_CARD_BILL = "queryCardBill";

	// 资产-信任宝
	public final static String SEEDID_CREDIT_PAY = "creditPay"; // 点击信任宝
	public final static String SEEDID_OPEN_CREDIT_PAY = "openCreditPay"; // 点击开通信任宝
	public final static String SEEDID_MANAGE_BUTTON = "manageButton"; // 点击管理信任宝
	public final static String SEEDID_SEE_DETAILS = "seeDetails"; // 点击查看明细
	public final static String SEEDID_BILL_ICON = "billIcon"; // 点击账单
	public final static String SEEDID_SEE_BILL = "seeBill"; // 点击查看账单
	public final static String SEEDID_REPAYMENT_BUTTON = "repaymentButton"; // 点击立即还款
	public final static String SEEDID_INPUT_MONEY = "inputMoney"; // 点击输入框
	public final static String SEEDID_CONFIRM_REPAYMENT_BUTTON = "confirmRepaymentButton"; // 点击确认还款

	public static int LOG_LEVEL = 4;

	public static int LOG_LEVEL_ERROR = 1;

	public static int LOG_LEVEL_WARNING = 2;

	public static int LOG_LEVEL_DEBUG = 3;

	public static int LOG_LEVEL_INFO = 4;

	public static int LOG_LEVEL_V = 5;

	public static final String CLIENTVERSION = "clientVersion";

	// 性能耗时埋点
	public final static String APPID_PERF = "perf";
	public final static String PERF_TYPE_LOGIN = "perf_login";
	public final static String PERF_TYPE_STARTUP = "perf_startup";
	public final static String PERF_TYPE_OPEN_ASSETS = "perf_open_assets"; // 打开资产perf
	public final static String PERF_TYPE_OPEN_BILLDETAIL = "perf_open_billdetail"; // 打开账单perf
	public final static String PERF_TYPE_OPEN_BILLDEAL = "perf_open_billdeal"; // 打开收支明细perf
	public final static String PERF_TYPE_OPEN_MYVOUCHER = "perf_open_myvoucher"; // 打开我的卡劵perf
	public final static String PERF_TYPE_OPEN_GAS = "perf_open_gas"; // 打开燃气费perf
	public final static String PERF_TYPE_OPEN_STATEMENTS = "perf_open_statements"; // 打开对账单perf

	// 邮箱账单埋点
	public final static String VIEWID_CCB_IMPORTBILLINDEX = "importBillIndex";// 账单导入引导页
	public final static String VIEWID_CCB_IMPORTBILLCONFIRM = "importBillConfirm";// 账单导入邮箱确认页
	public final static String VIEWID_CCB_IMPORTBILLRESULT = "importBillResult";// 账单导入结果页
	public final static String VIEWID_CCB_CARDBILLINDEX = "cardBillIndex";// 账单列表
	public final static String VIEWID_CCB_CARDBILLDETAILS = "cardBillDetails";// 账单详情
	public final static String VIEWID_CCB_EMAILMANAGE = "emailManage";// 邮箱管理
	public final static String VIEWID_CCB_SUPPORTEMAILVIEW = "supportEmailView";// 已支持邮箱页
	public final static String VIEWID_CCB_SUPPORTBANKVIEW = "supportBankView";// 已支持银行页
	public final static String VIEWID_CCB_IMPORTBILLDEALVIEW = "importBillDealView";// 导入账单协议页
	public final static String VIEWID_CCB_IMPORTBILLPROCESS = "importBillProcess";// 账单导入过程页
	public final static String VIEWID_CCB_CARDSETINDEX = "cardSetIndex";// 账单导入过程页
	public final static String VIEWID_CCB_SETEMAIL = "setEmail";// 账单导入过程页
	public final static String VIEWID_CCB_CARDREMINDVIEW = "cardRemindView";// 账单导入过程页
	public final static String VIEWID_CCB_TODOLIST = "todoList";

	public final static String SEEDID_CCB_BACKICON = "backIcon";
	public final static String SEEDID_CCB_INPUTEMAIL = "inputEmail";
	public final static String SEEDID_CCB_INPUTEMAILPWD = "inputEmailPwd";
	public final static String SEEDID_CCB_SUPPORTEMAIL = "supportEmail";
	public final static String SEEDID_CCB_SUPPORTBANK = "supportBank";
	public final static String SEEDID_CCB_IMPORTBILLDEAL = "importBillDeal";
	public final static String SEEDID_CCB_CONFIRMBUTTON = "confirmButton";
	public final static String SEEDID_CCB_CANCELBUTTON = "cancelButton";
	public final static String SEEDID_CCB_REIMPORTBUTTON = "reimportButton";
	public final static String SEEDID_CCB_SETICON = "setIcon";
	public final static String SEEDID_CCB_PAYMENT = "payment";
	public final static String SEEDID_CCB_SETPAYMENTREMIND = "setPaymentRemind";
	public final static String SEEDID_CCB_DELCARD = "delCard";
	public final static String SEEDID_CCB_ADDBUTTON = "addButton";
	public final static String SEEDID_CCB_SEEEMAIL = "seeEmail";
	public final static String SEEDID_CCB_IMPORTBUTTON = "importButton";
	public final static String SEEDID_CCB_SEEBILLDETAILS = "seeBillDetails";
	public final static String SEEDID_CCB_ENTERBILLBUTTON = "enterBillButton";
	public final static String SEEDID_CCB_SEETODO = "seeTodo";
	public final static String SEEDID_CCB_DELMAIL = "delMail";

	//拍卡成功页埋点
	public final static String VIEWID_PAIKA_10000007HOMEVIEW = "10000007HOME";
	public final static String VIEWID_PAIKA_RECOGNISECARDVIEW = "recogniseCardView";
	
	public final static String SEEDID_PAIKA_BARCODE = "barcode";
	public final static String SEEDID_PAIKA_QRCODE = "qrcode";
	public final static String SEEDID_PAIKA_CARD = "card";
	public final static String SEEDID_PAIKA_COPYDARD = "copyCard";
	public final static String SEEDID_PAIKA_OPENQUICKPAY = "openQuickPay";
	public final static String SEEDID_PAIKA_PAYCARD = "payCard";
	public final static String SEEDID_PAIKA_TRANSFER = "transfer";
}