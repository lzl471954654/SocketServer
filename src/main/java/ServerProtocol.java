public interface ServerProtocol {
    /*
    * HEART_BEAT            心跳
    * MAKE_HOLE             打洞
    * HOLE_SUCCESS          打洞成功
    * HOLE_FAILED           打洞失败
    * ONLINE                上线
    * OFFLINE               离线
    * CONNECTED_TO_USER     连接--用户
    * CONTROL               控制端
    * BE_CONTROLLED         被控制端
    * SYMMETRIC_NAT_MODE    对等NAT模式
    * ASYMMETRIC_NAT_MODE   不对等NAT模式
    * UDP_MODE              UDP模式
    * TCP_MODE              TCP模式
    * */
    public static final String HEATR_BEAT = "|BEAT|";
    public static final String MAKE_HOLE = "|HOLE|";
    public static final String HOLE_SUCCESS = "|HOLE@SUCCESS|";
    public static final String HOLE_FAILED = "|HOLE@FAIL|";
    public static final String ONLINE = "|ONLINE|";
    public static final String OFFLINE = "|OFFLINE|";
    public static final String CONNECTED_TO_USER = "|CONNECTED@TO@USER|";
    public static final String CONTROL = "|CONTROL|";
    public static final String BE_CONTROLLED = "|BE@CONTROLLED|";
    public static final String SYMMETRIC_NAT_MODE = "|SYMMETRIC@NAT|";
    public static final String ASYMMETRIC_NAT_MODE = "|ASYMMETRIC@NAT|";
    public static final String UDP_MODE = "|UDP@MODE|";
    public static final String TCP_MODE = "|TCP@MODE|";
    public static final String ERROR = "|ERROR|";
    public static final String NORMAL_MSG = "|NORMAL@MSG|";
    public static final String ONLINE_SUCCESS = "|ONLINE@SUCCESS|";
    public static final String ONLINE_FAILED = "|ONLINE_FAILED|";
    public static final String END_FLAG = "@@|END@FLAG|@@";
}
