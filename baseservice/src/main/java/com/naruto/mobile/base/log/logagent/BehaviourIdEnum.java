package com.naruto.mobile.base.log.logagent;

public enum BehaviourIdEnum {
    NONE("none"), 
    
    CLICKED("clicked"), // 瀹㈡埛绔墜鍔ㄩ噰闆嗛〉闈㈢偣鍑�
    OPENPAGE("openPage"), // 瀹㈡埛绔墜鍔ㄩ噰闆嗛〉闈㈡墦寮�
    
    LONGCLICKED("longClicked"), 
    
    AUTO_CLICKED("auto_clicked"), // 瀹㈡埛绔嚜鍔ㄩ噰闆嗛〉闈㈢偣鍑�
    AUTO_OPENPAGE("auto_openPage"), // 瀹㈡埛绔嚜鍔ㄩ噰闆嗛〉闈㈡墦寮�
    
    SUBMITED("submited"), 
    BIZLAUNCHED("bizLaunched"),
    ERROR("error"), 
    EXCEPTION("exception"), 
    SETGESTURE("setGesture"), 
    CHECKGESTURE("checkGesture"), 
    SLIDED("slided"), 
    MONITOR("monitor"),
    
    MONITORPERF("monitorPerf");		//性能检测

    private String desc;

    private BehaviourIdEnum(String desc) {
        this.desc = desc;
    }

    public String getDes() {
        return desc;
    }
    public static BehaviourIdEnum convert(String s)
    {
    	for(BehaviourIdEnum v:BehaviourIdEnum.values())
    	{
    		if(v.desc.equals(s))
    			return v;
    	}
    	return NONE;
    }
}
