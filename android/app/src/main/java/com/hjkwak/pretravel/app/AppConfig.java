package com.hjkwak.pretravel.app;

/**
 * Created by Hyojin on 2016-04-19.
 */


public class AppConfig {
    // Server user login url
    public static String URL_LOGIN = "http://115.71.232.67/android_login_api/login.php";

    // Server user register url
    public static String URL_REGISTER = "http://115.71.232.67/android_login_api/register.php";

    // Server emil authentication check url
    public static String URL_EMAIL= "http://115.71.232.67/android_login_api/sendmail.php";

    // Server emil authentication check url
    public static String URL_AUTHENTICATION= "http://115.71.232.67/android_login_api/athentication.php";

    //국가, 도시 찾기
    public static String URL_SETWHEREITIS="http://115.71.232.67/android_login_api/setWhere.php";

    //영화찾기
    public static String URL_SEARCHMOVIE="http://115.71.232.67/android_login_api/movieInfo.php";

    //영화찾기
    public static String URL_SEARCHBOOK="http://115.71.232.67/android_login_api/bookInfo.php";

    //국가, 도시 입력
    public static String URL_ADDWHERE="http://115.71.232.67/android_login_api/addwhere.php";

    //포스팅 업로드
    public static String URL_POSTUPLOAD= "http://115.71.232.67/android_login_api/PostUpload.php";

    //추천리스트 검색
    public static String URL_RECOMMENDLIST = "http://115.71.232.67/android_login_api/recommendList.php";

    // Server user login url
    public static String URL_SEARCH = "http://115.71.232.67/android_login_api/search.php";

    // 추천유저리스트 받아오기
    public static String URL_GETRECOMMENDUSERLIST = "http://115.71.232.67/android_login_api/getRecommendUserList.php";

    //추천유저리스트 업데이트
    public static String URL_PUSHRECOMMEND = "http://115.71.232.67/android_login_api/pushRecommend.php";

    // 이름 중복확인
    public static String URL_CHECKONLY = "http://115.71.232.67/android_login_api/checkOnlyName.php";

    // 로그인 시, 초기화
    public static String URL_SYNC = "http://115.71.232.67/android_login_api/sync.php";

    //추천수 가장 높은 컨텐츠 데이터 받아오기
    public static String URL_GETHIGH = "http://115.71.232.67/android_login_api/getHighRecommend.php";

    //스크랩리스트 가져오기
    public static String URL_GETSCRAPLIST = "http://115.71.232.67/android_login_api/getScrapList.php";

    //스크랩버튼 클릭
    public static String URL_PUSHSCRAP= "http://115.71.232.67/android_login_api/pushScrap.php";

    //컨텐츠 지우기
    public static String URL_DELETECONTENT= "http://115.71.232.67/android_login_api/deleteContents.php";

    //스크랩한 컨텐츠 정보 가져오기
    public static String URL_GETSCRAPCONTENT= "http://115.71.232.67/android_login_api/getContent.php";

    //Favorite 리스트 가져오기
    public static String URL_GETFAVORITELIST = "http://115.71.232.67/android_login_api/getFavoriteList.php";

    //Favorite 버튼 클릭
    public static String URL_PUSHFAVORITE= "http://115.71.232.67/android_login_api/pushFavorite.php";

    //유저별 글찾기
    public static String URL_BYUSER= "http://115.71.232.67/android_login_api/byUser.php";

    //팔로잉목록가져오기
    public static String URL_GETFOLLOWINGLIST = "http://115.71.232.67/android_login_api/getFollowList.php";

    //팔로우버튼클릭
    public static String URL_PUSHFOLLOW = "http://115.71.232.67/android_login_api/pushFollow.php";

    // 알람 및 토큰 세팅
    public static String URL_TOKEN = "http://115.71.232.67/android_login_api/alramSetting.php";

    // 알람 보내기
    public static String URL_SENDALRAM = "http://115.71.232.67/android_login_api/gcm_push_test.php";

    // 탈퇴하기
    public static String URL_REMOVEUSER = "http://115.71.232.67/android_login_api/removeUser.php";


}

