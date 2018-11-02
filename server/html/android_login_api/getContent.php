<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['text_uid']) && isset($_POST['con_title'])) {
    // receiving the post params
    $text_uid = $_POST['text_uid'];
    $con_title = $_POST['con_title'];

    // get the user by email and password
    $contents = $db->searchContent($text_uid, $con_title);
        if ($contents) {
        // use is found
        $response["error"] = FALSE;
          $response["user_name"] = $contents["user_name"];
          $response["text_uid"] = $contents["text_uid"];
          $response["country"] = $contents["country"];
          $response["city"] = $contents["city"];
          $response["con_title"] = $contents["con_title"];
          $response["con_data1"]=$contents["con_data1"];
          $response["con_data2"] = $contents["con_data2"];
          $response["con_data3"]= $contents["con_data3"];
          $response["con_data4"]=$contents["con_data4"];
          $response["con_photo"]=$contents["con_photo"];
          $response["created_at"]=$contents["created_at"];
          $response["recommend"] =$contents["recommend"];
        
        echo json_encode($response);
    } else {
        // user is not found with the credentials
        $response["error"] = TRUE;
        $response["error_msg"] = "데이터가 없습니다.";
        echo json_encode($response);
    }

}else{
// required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters are missing!";
    echo json_encode($response);
}

?>


