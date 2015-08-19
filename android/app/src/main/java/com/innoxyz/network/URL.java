package com.innoxyz.network;

/**
 * 所有访问的URL均定义在这里。
 */
public class URL {

    public static String host="http://www.innoxyz.com";


    /*******************************************登录相关****************************************/
    //post: email+password
    public static final String LOGIN = host + "/user/login.action";
    //GET
    public static final String LOGOUT = host + "/user/logout.action";
    /*****************************************************************************************/

    //下载文件
    public static String  GET_FILE = host + "/file/file.action";

    /*******************************************文件相关****************************************/
    //上传文档 POST
    public static String  UPLOAD_DOCUMENT = host + "/file/upload-document.action";
    // 根据项目ID获取存储目录 GET  thingId ( i.e. project id )
    public static String GET_BASEDIR_OF_PROJECT = host + "/thing/lproject/get-basedir.action" ;
    //根据ID获取文档数据 GET entryId
    public static String LIST_DOCUMENT_IN_PROJECT = host + "/thing/list-document-json.action";
    /*****************************************************************************************/

    /*******************************************通知****************************************/
    // 获取未读消息数 GET parameters: read(=false)
    public static String LIST_NOTIFY_COUNT = host + "/notify/count";
    //获取通知列表，get，参数read(=true, false)
    public static String LIST_NOTIFY = host + "/notify/list";
    //将通知设为已读，parameters: thingId thingType value(false=unread->read) POST
    public static String SET_NOTIFY_READ = host + "/notify/readAll";
    // 在指定项目中获取所有通知  GET  parameters: thingId page
    public static String LIST_ANNOUNCEMENT_IN_PROJECT = host + "/thing/lproject/announcement/list.action";
    /*****************************************************************************************/



    /***********************************项目**********************************************/
    /**
     * 根据项目ID获取项目基本信息
     * GET
     * http://innoxyz.com/thing/lproject/get-info.action?thingId=815
     */
    public static String  GET_INFO_OF_LPROJECT = host + "/thing/lproject/get-info.action";
    // 获取参与全部项目列表 GET pageSize page archive=false 搜索namelike
    public static String LIST_JOINED_LPROJECT = host + "/thing/lproject/list-joined-lproject.action";
    // 获取加星项目列表 GET pageSize page
    public static String LIST_LIKED_LPROJECT = host + "/thing/lproject/list-liked-lproject";
    //加星 POST type2 thingId2 add
    public static String LINK_LIKE = host + "/thing/link-like";
    /*****************************************************************************************/

    /*******************************************任务****************************************/
    //回复任务 POST
    public static String TASK_REPLY = host + "/thing/task/pushstate/new.action";
    //state:{"taskId":"532d84b673f2fa1479576e6b","hostId":"545","hostType":"LPROJECT","comment":"456","modifications":{"state":{"newValue":"IN_PROGRESS"}}}
    //attachments:[] 已上传的附件ID

    // 获取任务所有回复 parameters: taskId ;method:get
    public static String LIST_PUSHSTATE = host + "/thing/task/pushstate/list";
    // 在指定项目中获取所有任务  GET  parameters: hostId ( i.e. project id ) page
    public static String LIST_TASK_IN_LPROJECT = host + "/thing/task/list.action";
    /*****************************************************************************************/

    /*******************************************话题****************************************/
    //回复话题 POST
    public static String TOPIC_REPLY = host + "/thing/topic/new-post.action";
    //topicId:54753ba70cf20517fd0d09d1
    //content:阿道夫
    //attachments:["5493fc6b0cf24955326b6821"]

    // parameters: topicId GET
    public static String TOPIC_DETAIL = host + "/thing/topic/topic.action";
    // 在指定项目中获取所有话题  GET  parameters: lprojectId ( i.e. project id ) page
    public static String LIST_TOPIC_IN_LPROJECT = host + "/thing/topic/list.action";
    /*****************************************************************************************/

    /*******************************************用户****************************************/
    //根据用户ID获取用户头像
    public static String getUserAvatarURL(int id){
        return host + "/file/file.action?id=" + id + "&type=user_avatar";
    }
    //GET
    public static String USER_PROFILE = host + "/user/profile.action";
    /*****************************************************************************************/

    /*******************************************私信****************************************/
    //获取收件箱 GET
    public static String LIST_MESSAGE = host + "/message/list.action";
    //获取发件箱 GET
    public static String LIST_SENT_MESSAGE = host + "/message/list-sent.action";
    /*****************************************************************************************/

    /*******************************************成员****************************************/
    // 根据项目ID获取参与人员 GET  thingId pageSize
    public static String GET_MEMBER_OF_PROJECT = host + "/thing/lproject/list-member.action";
    /*****************************************************************************************/


}
