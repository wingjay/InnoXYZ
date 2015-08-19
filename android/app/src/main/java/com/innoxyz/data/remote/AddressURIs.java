package com.innoxyz.data.remote;

import com.innoxyz.network.URL;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-7
 * Time: 下午3:51
 * To change this template use File | Settings | File Templates.
 */
public class AddressURIs {
    //public static final String HOST = "http://202.120.54.161:2580";
    public static final String HOST = URL.host;

    public static final RequestInfo LOGIN = new RequestInfo("/user/login.action", "post");
    public static final RequestInfo LOGOUT = new RequestInfo("/user/logout.action", "get");

    public static final RequestInfo LIST_NOTIFY = new RequestInfo("/notify/list.action", "get");
    // parameters: read(=true, false)
    public static final RequestInfo LIST_NOTIFY_COUNT = new RequestInfo("/notify/count", "get");
    // parameters: read(=true, false)
    public static final RequestInfo LIST_MESSAGE_COUNT = new RequestInfo("/message/count", "get");
    // parameters: thingId thingType value(false=unread->read)
    public static final RequestInfo SET_NOTIFY_READ = new RequestInfo("/notify/readAll", "post");


    // parameters: none
    public static final RequestInfo LIST_JOINED_LPROJECT = new RequestInfo("/thing/lproject/list-joined-lproject.action", "get");
    // parameters: none
    public static final RequestInfo LIST_LIKED_LPROJECT = new RequestInfo("/thing/lproject/list-liked-lproject", "get");
    // parameters: hostId ( i.e. project id )
    public static final RequestInfo LIST_TASK_IN_LPROJECT = new RequestInfo("/thing/task/list.action", "get");
    // parameters: lprojectId
    public static final RequestInfo LIST_TOPIC_IN_LPROJECT = new RequestInfo("/thing/topic/list.action", "get");

    // parameters: pageSize thingId2 type2
    public static final RequestInfo GET_INVOLVES = new RequestInfo("/link/get-involves.action", "get");
//    pageSize:100
//    thingId2:532002b673f2fa147956a4be
//    type2:TOPIC

    // parameters: userIds thingId2 type2
    public static final RequestInfo ADD_INVOLVES = new RequestInfo("/link/involve.action", "post");
//    userIds:["883"]
//    thingId2:5262a0072cdcddcd0fac9381
//    type2:TOPIC / TASK

    // parameters: thingIds type1 thingId2 type2 linkType
    public static final RequestInfo REMOVE_INVOLVES = new RequestInfo("/link/remove.action", "post");
//    thingIds:["883"]
//    type1:USER
//    thingId2:5262a0072cdcddcd0fac9381
//    type2:TOPIC / TASK
//    linkType:INVOLVED

    // parameters : thingId pageSize
    public static final RequestInfo GET_MEMBER_OF_PROJECT = new RequestInfo("/thing/lproject/list-member.action", "get");
    //http://innoxyz.com/thing/lproject/list-member.action?thingId=464&pageSize=100

    // parameters: topicId
    public static final RequestInfo TOPIC_DETAIL = new RequestInfo("/thing/topic/topic.action", "get");
    // parameters:topicId content attachments
    public static final RequestInfo TOPIC_REPLY = new RequestInfo("/thing/topic/new-post.action", "post");
    // parameters: thingId thingType subject content attachments userIds
    public static final RequestInfo TOPIC_NEW = new RequestInfo("/thing/topic/new.action", "post");

    // parameters: task attachments
    public static final RequestInfo TASK_NEW = new RequestInfo("/thing/task/create.action", "post");

    //task:{"hostId":"302","name":"aaa","priority":"LOW","state":"NEW","deadline":"2014-04-01T00:00:00.000Z","description":"bbb","assignees":[{"id":883},{"id":8}]}
    //userIds:[891,539]
    //attachments:[]

    //min:
    //task:{"hostId":"302","name":"aaa","priority":"LOW","state":"NEW","description":"vvv","assignees":[]}
    //task={"hostId":"545","name":"啊","priority":"HIGH","state":"NEW","description":"吧","assignees":[]}, attachments=[]
    //attachments:[]

    // parameters: state attachments
    public static final RequestInfo TASK_REPLY = new RequestInfo("/thing/task/pushstate/new.action", "post");
//    state:{"taskId":"532d84b673f2fa1479576e6b","hostId":"545","hostType":"LPROJECT","comment":"456","modifications":{"state":{"newValue":"IN_PROGRESS"}}}
//    attachments:[]



    // parameters:to subject contents threadid revicers emailNotify(false)
    public static final RequestInfo MESSAGE_NEW = new RequestInfo("/message/new.action", "post");
//    to:["7","76"]
//    subject:
//    contents:
//    threadid:
//    recivers:
//    emailNotify:false

    // parameters:
    public static final RequestInfo ANNOUNCEMENT_NEW = new RequestInfo("/thing/lproject/announcement/create.action","post");
//    thingId:545
//    title:123
//    content:456
//    emailNotify:false
//    attachments:[]
//    userIds:["7"]

    // parameters:term
    public static final RequestInfo USER_AUTOCOMPLETE = new RequestInfo("/user/user-autocomplete.action", "post");

    // parameters: thingId ( i.e. project id )
    public static final RequestInfo GET_BASEDIR_OF_PROJECT = new RequestInfo("/thing/lproject/get-basedir.action", "get");
    // parameters: entryId
    public static final RequestInfo LIST_DOCUMENT_IN_PROJECT = new RequestInfo("/thing/list-document-json.action", "get");
    // parameters: taskId
    public static final RequestInfo LIST_PUSHSTATE = new RequestInfo("/thing/task/pushstate/list", "get");
    // parameters: thingId
    public static final RequestInfo LIST_ANNOUNCEMENT_IN_PROJECT = new RequestInfo("/thing/lproject/announcement/list.action", "get");

    public static final RequestInfo LIST_MESSAGE = new RequestInfo("/message/list.action", "get");
    public static final RequestInfo LIST_SENT_MESSAGE = new RequestInfo("/message/list-sent.action", "get");

    public static final RequestInfo USER_PROFILE = new RequestInfo("/user/profile.action", "get");

    // parameters: type2 thingId2 add
    public static final RequestInfo LINK_LIKE = new RequestInfo("/thing/link-like", "post");
//    type2:LPROJECT
//    thingId2:545
//    add:false (star -> unstar)

    // parameters: username email message
    public static final RequestInfo INVITE = new RequestInfo("/invite/invite.action", "post");
//    username:Yi Wang
//    email:wangwlk@sina.com
//    message:你好

    // parameters: passwordOld passwordNew passwordAgain
    public static final RequestInfo CHANGE_PASS = new RequestInfo("/user/changepass.action", "post");



    //volley
    //根据用户ID获取用户头像
    public static String getUserAvatarURL(int id){
        return AddressURIs.HOST + "/file/file.action?id=" + id + "&type=user_avatar";
    }

    //下载文件
    public static final RequestInfo GET_FILE = new RequestInfo("/file/file.action","get");
}
