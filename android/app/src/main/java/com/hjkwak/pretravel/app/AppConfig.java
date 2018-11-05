package com.hjkwak.pretravel.app;

/**
 * Created by Hyojin on 2016-04-19.
 */


public class AppConfig {
    
    private static String ServerAPI = "http://d6ed12ce.ngrok.io/android_login_api/";


    // Server user login url
    public static String URL_LOGIN = ServerAPI + "login.php";

    // Server user register url
    public static String URL_REGISTER = ServerAPI + "register.php";

    // Server emil authentication check url
    public static String URL_EMAIL= ServerAPI + "sendmail.php";

    // Server emil authentication check url
    public static String URL_AUTHENTICATION= ServerAPI + "athentication.php";

    //국가, 도시 찾기
    public static String URL_SETWHEREITIS=ServerAPI + "setWhere.php";

    //영화찾기
    public static String URL_SEARCHMOVIE=ServerAPI + "movieInfo.php";

    //영화찾기
    public static String URL_SEARCHBOOK=ServerAPI + "bookInfo.php";

    //국가, 도시 입력
    public static String URL_ADDWHERE=ServerAPI + "addwhere.php";

    //포스팅 업로드
    public static String URL_POSTUPLOAD= ServerAPI + "PostUpload.php";

    //추천리스트 검색
    public static String URL_RECOMMENDLIST = ServerAPI + "recommendList.php";

    // Server user login url
    public static String URL_SEARCH = ServerAPI + "search.php";

    // 추천유저리스트 받아오기
    public static String URL_GETRECOMMENDUSERLIST = ServerAPI + "getRecommendUserList.php";

    //추천유저리스트 업데이트
    public static String URL_PUSHRECOMMEND = ServerAPI + "pushRecommend.php";

    // 이름 중복확인
    public static String URL_CHECKONLY = ServerAPI + "checkOnlyName.php";

    // 로그인 시, 초기화
    public static String URL_SYNC = ServerAPI + "sync.php";

    //추천수 가장 높은 컨텐츠 데이터 받아오기
    public static String URL_GETHIGH = ServerAPI + "getHighRecommend.php";

    //스크랩리스트 가져오기
    public static String URL_GETSCRAPLIST = ServerAPI + "getScrapList.php";

    //스크랩버튼 클릭
    public static String URL_PUSHSCRAP= ServerAPI + "pushScrap.php";

    //컨텐츠 지우기
    public static String URL_DELETECONTENT= ServerAPI + "deleteContents.php";

    //스크랩한 컨텐츠 정보 가져오기
    public static String URL_GETSCRAPCONTENT= ServerAPI + "getContent.php";

    //Favorite 리스트 가져오기
    public static String URL_GETFAVORITELIST = ServerAPI + "getFavoriteList.php";

    //Favorite 버튼 클릭
    public static String URL_PUSHFAVORITE= ServerAPI + "pushFavorite.php";

    //유저별 글찾기
    public static String URL_BYUSER= ServerAPI + "byUser.php";

    //팔로잉목록가져오기
    public static String URL_GETFOLLOWINGLIST = ServerAPI + "getFollowList.php";

    //팔로우버튼클릭
    public static String URL_PUSHFOLLOW = ServerAPI + "pushFollow.php";

    // 알람 및 토큰 세팅
    public static String URL_TOKEN = ServerAPI + "alramSetting.php";

    // 알람 보내기
    public static String URL_SENDALRAM = ServerAPI + "gcm_push_test.php";

    // 탈퇴하기
    public static String URL_REMOVEUSER = ServerAPI + "removeUser.php";


}

