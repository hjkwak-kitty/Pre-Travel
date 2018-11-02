<?php

class DB_Functions {

    private $conn;
	private $url = 'https://android.googleapis.com/gcm/send';
        private $serverApiKey = "";
        private $devices = array();
        private $notification_type;
	 function GCMPushMessage($apiKeyIn, $is_notification){
                $this->serverApiKey = $apiKeyIn;
                $this->notification_type = $is_notification;
        //      echo $apiKeyIn.$is_notification;
        }

        function setDevices($deviceIds){
                if(is_array($deviceIds)){
                        $this->devices = $deviceIds;
                } else{
                        $this->devices = array($deviceIds);
                }
        }

        function send($title,$message,$extra){
                if(!is_array($this->devices) || count($this->devices)== 0){
                        $this->error("No devices set");
                }
                if(strlen($this->serverApiKey) < 8){
                        $this->error("Server API Key not set");
                }

                $fields = array(
                        'registration_ids' => $this->devices,
                        'data' => array('title' =>$title, 'message' => $message, 'notification_type' => $this->notification_type, 'extra' =>$extra),);

                //echo json_encode($fields);
                //exit;

                        $headers =array(
                                'Authorization: key =' . $this->serverApiKey,
                                'Content-Type: application/json'
                        );
        //Open connection
                        $ch =curl_init();

                        //Set te url, number of Post vars, POST data
                        curl_setopt($ch, CURLOPT_URL, $this->url);
                        curl_setopt($ch, CURLOPT_POST, true);
                        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
                        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

                //Execute post
                $result = curl_exec($ch);

                //Close connection
                curl_close($ch);

                return $result;
        }

        function error($msg){
                echo "Android send notification failed with error:";
                echo "\t".$msg;
                exit(1);
        }


    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database


        $db = new Db_Connect();
        $this->conn = $db->connect();
    }

    // destructor
    function __destruct() {

    }

    /**
     * Storing new user
     * returns user details
     */

    public function storeUser($name, $email, $password) {
        $uuid = uniqid('', true);
        $hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt

        $stmt = $this->conn->prepare("INSERT INTO users(unique_id, name, email, encrypted_password, salt, created_at) VALUES(?, ?, ?, ?, ?, NOW())");
        $stmt->bind_param("sssss", $uuid, $name, $email, $encrypted_password, $salt);
        $result = $stmt->execute();
        $stmt->close();

        // check for successful store
        if ($result) {
            $conn = mysqli_connect("localhost", "root", "letmesee55!", "android_api");
            $query = "SELECT * FROM users where email='$email'";
            $result = mysqli_query($conn, $query);
            $user = mysqli_fetch_assoc($result);
            return $user;
            mysqli_close($conn);
        } else {
            return false;
        }
    }



    /**
     * Storing where
     * county and city
     */
    public function storeWhere($country, $city) {
        $stmt = $this->conn->prepare("INSERT INTO toWhere(country, city) VALUES(?, ?)");
        $stmt->bind_param("ss", $country, $city);
        $result = $stmt->execute();
        $stmt->close();

    }






    /**
     * Make some Text
     * For Authentication
     */
    public function getAuthText($origin) {

        $hash = $this->hashSSHA($origin);
        $encrypted_text = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt

        return $hash;
    }


    /**
     * Get user by email and password
     */
    public function getUserByEmailAndPassword($email, $password) {
        $conn = mysqli_connect("localhost", "root", "letmesee55!", "android_api");
        $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");

        $stmt->bind_param("s", $email);

        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            // verifying user password
            $salt = $user['salt'];
            $encrypted_password = $user['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // user authentication details are correct
                return $user;
            }
        } else {
            return NULL;
        }
    }

    /**
     * Check user is existed or not
     */
    public function isUserExisted($email) {
        $stmt = $this->conn->prepare("SELECT email from users WHERE email = ? OR name = ?");

        $stmt->bind_param("ss", $email,$email);

        $stmt->execute();

        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            // user existed
            $stmt->close();
            return true;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
    }


    /**
     * 해당 장소의 존재 여부
     **/
    public function isWhereExisted($where) {
        $conn = mysqli_connect("localhost", "root", "letmesee55!", "android_api");
        $stmt = $this->conn->prepare("SELECT country from toWhere WHERE country like '%$where%'");
        //$stmt->bind_param("s", $where);

        $stmt->execute();
        $country = $stmt->get_result()->fetch_assoc();

        if($country){
            // user existed
            $stmt->close();
            return $country;
        } else {
            $stmt = $this->conn->prepare("SELECT country, city from toWhere WHERE city LIKE '%$where%'");
            //$stmt->bind_param("s", $where);
            $stmt->execute();
            $country = $stmt->get_result()->fetch_assoc();
            if($country){
                //echo $country[city];
                $stmt->close();
                return $country;
            }else{

                // user not existed
                $stmt->close();
                return false;
            }
        }
    }






    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {
        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }


    /**
     * Encrypting same password with salt
     *
     * and Check the authentication
     */
    public function checkAuth($origin, $salt) {
        $encrypted = base64_encode(sha1($origin . $salt, true) . $salt);
        $hash = array("encrypted" => $encrypted, "salt"=>$salt);
        return $hash;
    }





    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {

        $hash = base64_encode(sha1($password . $salt, true) . $salt);

        return $hash;
    }


    /**
     * 콘텐츠 업로드
     *
     */

    public function uploadPost($contents,$size) {

        for($i=0;$i<$size;$i++){
            $stmt = $this->conn->prepare("INSERT INTO contents(recommend,user_name,text_uid,country, city, con_title, con_data1, con_data2, con_data3, con_data4, con_photo, created_at) VALUES(0,?,?,?,?,?,?,?,?,?,?, NOW())");
            $stmt->bind_param("ssssssssss", $contents["user_name"][$i],$contents["text_uid"][$i],$contents["country"][$i],$contents["city"][$i],$contents["con_title"][$i],$contents["con_data1"][$i],$contents["con_data2"][$i],$contents["con_data3"][$i],$contents["con_data4"][$i],$contents["con_photo"][$i]);
            $result = $stmt->execute();
            $stmt->close();
        }
        if($result){
            return true;
        } else{
            return false;
        }
    }
 
 /**
     *  컨텐츠 지우기,
     */

    public function deleteContent($text_uid) {
      $stmt = $this->conn->prepare("delete from contents where text_uid = '$text_uid'");
      //$stmt->bind_param("s",$text_uid);
      $result = $stmt->execute();
        
        if($result){
            return true;
        } else{
            return false;
        }
    }
/**
     * 딴유저글검색
     */
    public function byUser($name) {
        $conn = mysqli_connect("localhost", "root", "letmesee55!", "android_api");
        $stmt= "SELECT * FROM contents where user_name='$name' group by text_uid";

        if ($result = mysqli_query($conn,$stmt)){
            //$o = array();
            $i=0;
            while ($row = mysqli_fetch_object($result)) {
                        /*$t = new stdClass();
                        $t->user_name = $row->user_name;
                        $t->text_id = $row->text_id;
                        $t->country = $row->country;
                        $o[] = $t;
                        unset($t);*/
                $contents[$i]["user_name"]=$row->user_name;
                $contents[$i]["text_uid"]=$row->text_uid;
                $contents[$i]["country"]=$row->country;
                $contents[$i]["city"]=$row->city;
                $contents[$i]["con_title"]=$row->con_title;
                $contents[$i]["con_data1"]=$row->con_data1;
                $contents[$i]["con_data2"]=$row->con_data2;
                $contents[$i]["con_data3"]=$row->con_data3;
                $contents[$i]["con_data4"]=$row->con_data4;
                $contents[$i]["con_photo"]=$row->con_photo;
                $contents[$i]["created_at"]=$row->created_at;
                $contents[$i]["recommend"]=$row->recommend;
                $i++;
            }
            $contents["size"]=$i;
        } else {
            return $contents;
        }
        mysqli_close($conn);
        return $contents;
    }



    /**
     * 검색
     */
    public function searchContents($country, $city) {
        $conn = mysqli_connect("localhost", "root", "letmesee55!", "android_api");
        if($city!="없다"){
            $stmt= "SELECT * FROM contents where country='$country' and city='$city' group by con_title order by recommend desc";
        }else{
            $stmt= "SELECT * FROM contents where country='$country' group by con_title order by recommend desc";
        }

        if ($result = mysqli_query($conn,$stmt)){
            //$o = array();
            $i=0;
            while ($row = mysqli_fetch_object($result)) {
			/*$t = new stdClass();
			$t->user_name = $row->user_name;
			$t->text_id = $row->text_id;
			$t->country = $row->country;
			$o[] = $t;
			unset($t);*/
                $contents[$i]["user_name"]=$row->user_name;
                $contents[$i]["text_uid"]=$row->text_uid;
                $contents[$i]["country"]=$row->country;
                $contents[$i]["city"]=$row->city;
                $contents[$i]["con_title"]=$row->con_title;
                $contents[$i]["con_data1"]=$row->con_data1;
                $contents[$i]["con_data2"]=$row->con_data2;
                $contents[$i]["con_data3"]=$row->con_data3;
                $contents[$i]["con_data4"]=$row->con_data4;
                $contents[$i]["con_photo"]=$row->con_photo;
                $contents[$i]["created_at"]=$row->created_at;
                $contents[$i]["recommend"]=$row->recommend;
                $i++;
            }
            $contents["size"]=$i;
        } else {
            return $contents;
        }
        mysqli_close($conn);
        return $contents;
    }


    /**
     *  추천리스트 검색
     */
    public function searchRecommendList($country, $city, $user_name) {
        $conn = mysqli_connect("localhost", "root", "letmesee55!", "android_api");
        if($city!="없다"){
            $stmt= "SELECT * FROM contents where country='$country' and city='$city' and user_name='$user_name' group by con_title order by recommend desc";
        }else{
            $stmt= "SELECT * FROM contents where country='$country' and user_name='$user_name' group by con_title order by recommend desc";
        }

        if ($result = mysqli_query($conn,$stmt)){
            //$o = array();
            $i=0;
            while ($row = mysqli_fetch_object($result)) {
                        /*$t = new stdClass();
                        $t->user_name = $row->user_name;
                        $t->text_id = $row->text_id;
                        $t->country = $row->country;
                        $o[] = $t;
                        unset($t);*/
                $contents[$i]["user_name"]=$row->user_name;
                $contents[$i]["text_uid"]=$row->text_uid;
                $contents[$i]["country"]=$row->country;
                $contents[$i]["city"]=$row->city;
                $contents[$i]["con_title"]=$row->con_title;
                $contents[$i]["con_data1"]=$row->con_data1;
                $contents[$i]["con_data2"]=$row->con_data2;
                $contents[$i]["con_data3"]=$row->con_data3;
                $contents[$i]["con_data4"]=$row->con_data4;
                $contents[$i]["con_photo"]=$row->con_photo;
                $contents[$i]["created_at"]=$row->created_at;
                $contents[$i]["recommend"]=$row->recommend;
                $i++;
            }
            $contents["size"]=$i;
        } else {
            return $contents;
        }
        mysqli_close($conn);
        return $contents;
    }



/**
     * 내글검색
     */
    public function sync($name) {
        $conn = mysqli_connect("localhost", "root", "letmesee55!", "android_api");
        $stmt= "SELECT * FROM contents where user_name='$name'";

        if ($result = mysqli_query($conn,$stmt)){
            //$o = array();
            $i=0;
            while ($row = mysqli_fetch_object($result)) {
			/*$t = new stdClass();
			$t->user_name = $row->user_name;
			$t->text_id = $row->text_id;
			$t->country = $row->country;
			$o[] = $t;
			unset($t);*/
                $contents[$i]["user_name"]=$row->user_name;
                $contents[$i]["text_uid"]=$row->text_uid;
                $contents[$i]["country"]=$row->country;
                $contents[$i]["city"]=$row->city;
                $contents[$i]["con_title"]=$row->con_title;
                $contents[$i]["con_data1"]=$row->con_data1;
                $contents[$i]["con_data2"]=$row->con_data2;
                $contents[$i]["con_data3"]=$row->con_data3;
                $contents[$i]["con_data4"]=$row->con_data4;
                $contents[$i]["con_photo"]=$row->con_photo;
                $contents[$i]["created_at"]=$row->created_at;
                $contents[$i]["recommend"]=$row->recommend;
                $i++;
            }
            $contents["size"]=$i;
        } else {
            return $contents;
        }
        mysqli_close($conn);
        return $contents;
    }

    /**
     * 추천 버튼 누름
     */
   public function pushRecommend($text_uid, $con_title, $user, $howTo) {	
        //echo $text_uid." ".$con_title." ".$user."22";
	//iconv('euc-kr', 'utf-8', $con_title);
	$stmt = $this->conn->prepare("update contents set recommendList=?, recommend=? where text_uid = '$text_uid' and con_title = '$con_title'");
	$stmt->bind_param("ss", $user, $howTo);
        $result = $stmt->execute();
        $stmt->close();

        if($result){
            return true;
        } else{
            return false;
        }

    }
    /**
     * 특정 글 검색
     */
    public function searchContent($text_uid, $con_title) {
        $conn = mysqli_connect("localhost", "root", "letmesee55!", "android_api");
        $stmt = $this->conn->prepare("SELECT * FROM contents WHERE text_uid = ? and con_title=?");

        $stmt->bind_param("ss", $text_uid, $con_title);

        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $user;
        
        } else {
            return NULL;
        }
    }
 /**
     *  추천리스트 검색
     */
    public function getRecommendUserList($text_uid, $con_title) {
        $conn = mysqli_connect("localhost", "root", "letmesee55!", "android_api");
	//echo $text_uid."+".$con_title;
	//$text_uid = base64_decode($text_uid);
	//$con_title = base64_decode($con_title);
        $stmt = $this->conn->prepare("SELECT recommendList FROM contents where text_uid = '$text_uid' and con_title = '$con_title'");

        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
	    return $user;

        } else {
            return NULL;
        }
    }



 /**
     *  스크랩 리스트 검색
     */
    public function getScrapList($user_name) {
        $conn = mysqli_connect("localhost", "root", "letmesee55!", "android_api");
        echo $user_name;
        $stmt = $this->conn->prepare("SELECT * FROM users where name = ?");
	$stmt->bind_param("s", $user_name);


        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
            return $user;

        } else {
            return NULL;
        }
    }

   /**
     * 스크랩 버튼 누름
     */
   public function pushScrap($scrap_uid, $scrap_title, $user) {
        echo $scrap_uid." ".$scrap_title." ".$user."22";
        //iconv('euc-kr', 'utf-8', $con_title);
        $stmt = $this->conn->prepare("update users set scrap_uid = '$scrap_uid',  scrap_title = '$scrap_title' where name = '$user'");
        //$stmt->bind_param("ss", $scrap_uid, $scrap_title);
        $result = $stmt->execute();
        $stmt->close();

        if($result){
            return true;
        } else{
            return false;
        }

    }


/**
     * FAVORITE 버튼 누름
     */
   public function pushFavorite($favorite_country, $favorite_city, $user) {
        echo $scrap_uid." ".$scrap_title." ".$user."22";
        //iconv('euc-kr', 'utf-8', $con_title);
        $stmt = $this->conn->prepare("update users set favorite_country = '$favorite_country',  favorite_city = '$favorite_city' where name = '$user'");
        $result = $stmt->execute();
        $stmt->close();

        if($result){
            return true;
        } else{
            return false;
        }

    }


 /**
     *  광고 추천수 많은 부분
     */
    public function getHighRecommendContent() {
        $conn = mysqli_connect("localhost", "root", "letmesee55!", "android_api");
        //echo $text_uid."+".$con_title;
        //$text_uid = base64_decode($text_uid);
        //$con_title = base64_decode($con_title);
        $stmt = $this->conn->prepare("SELECT * FROM contents where con_data1='movie' order by recommend desc");
        if ($stmt->execute()) {
            $user["movie"] = $stmt->get_result()->fetch_assoc();
            $stmt->close();
	$stmt = $this->conn->prepare("SELECT * FROM contents where con_data1='book' order by recommend desc");
$stmt->execute();
$user["book"] = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $user;

        } else {
            return NULL;
        }
    }

    /**
     * follower수정
     */
    public function pushFollow($writer, $write_follower) {
        
	echo $writer.$write_follower;

        //iconv('euc-kr', 'utf-8', $con_title);
        $stmt = $this->conn->prepare("update users set follower = '$write_follower' where name = '$writer'");
        //$stmt->bind_param("ss", $scrap_uid, $scrap_title);
        $result = $stmt->execute();
        $stmt->close();

        if($result){
            return true;
        } else{
            return false;
        }

    }


     /**
     * follow수정
     */
    public function pushFollower($user_name, $user_follow) {

        echo $user_name.$user_follow;

      
        $stmt = $this->conn->prepare("update users set follow = '$user_follow' where name = '$user_name'");
        $result = $stmt->execute();
        $stmt->close();

        if($result){
            return true;
        } else{
            return false;
        }

    }

    /**
     * 알람셋팅
     */
    public function alramSetting($user_name, $gcm, $token) {

        echo $user_name.$gcm.$token;

      
        $stmt = $this->conn->prepare("update users set gcm = '$gcm', token='$token' where name = '$user_name'");
        $result = $stmt->execute();
        $stmt->close();

        if($result){
            return true;
        } else{
            return false;
        }

    }
   /**
     * 탈퇴하기
     */
   public function removeUser($user) {
        echo $user."22";
        //iconv('euc-kr', 'utf-8', $con_title);
        $stmt = $this->conn->prepare("delete from users where name = '$user'");
        //$stmt->bind_param("ss", $scrap_uid, $scrap_title);
        $result = $stmt->execute();
        $stmt->close();

        if($result){
            return true;
        } else{
            return false;
        }

    }



}

?>
