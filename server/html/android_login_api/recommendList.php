<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['country']) && isset($_POST['city'])&& isset($_POST['user_name'])) {
    // receiving the post params
    $country = $_POST['country'];
    $city = $_POST['city'];
    $user_name = $_POST['user_name'];

    // get the user by email and password
    $contents = $db->searchRecommendList($country, $city, $user_name);	
	if ($contents&& $contents["size"]!=0) {
        // use is found
        $response["error"] = FALSE;
	$response["size"]= $contents["size"];
        for($i=0;$i<$contents["size"];$i++){
          $response["user_name"][$i] = $contents[$i]["user_name"];
          $response["text_uid"][$i] = $contents[$i]["text_uid"];
          $response["country"][$i] = $contents[$i]["country"];
          $response["city"][$i] = $contents[$i]["city"];
          $response["con_title"][$i] = $contents[$i]["con_title"];
          $response["con_data1"][$i]=$contents[$i]["con_data1"];
          $response["con_data2"][$i] = $contents[$i]["con_data2"];
	  $response["con_data3"][$i]=$contents[$i]["con_data3"];
          $response["con_data4"][$i]=$contents[$i]["con_data4"];
          $response["con_photo"][$i]=$contents[$i]["con_photo"];
          $response["created_at"][$i]=$contents[$i]["created_at"];
          $response["recommend"][$i] =$contents[$i]["recommend"];
        }
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
