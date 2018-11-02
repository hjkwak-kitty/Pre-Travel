<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

		
	$user = $db->getHighRecommendContent();
	//echo $recommendList['recommendList'];
	$contents[0] = $user["movie"];
	$contents[1] = $user["book"];
	if ($user) {
          $response["error"] = FALSE;
	  $response["user_name"] = $contents[0]["user_name"];
          $response["text_uid"] = $contents[0]["text_uid"];
          $response["country"] = $contents[0]["country"];
          $response["city"] = $contents[0]["city"];
          $response["con_title"] = $contents[0]["con_title"];
          $response["con_data1"]=$contents[0]["con_data1"];
          $response["con_data2"] = $contents[0]["con_data2"];
          $response["con_data3"]=$contents[0]["con_data3"];
          $response["con_data4"]=$contents[0]["con_data4"];
          $response["con_photo"]=$contents[0]["con_photo"];
          $response["created_at"]=$contents[0]["created_at"];
          $response["recommend"] =$contents[0]["recommend"];
	
	  $response["user_name_2"] = $contents[1]["user_name"];
          $response["text_uid_2"] = $contents[1]["text_uid"];
          $response["country_2"] = $contents[1]["country"];
          $response["city_2"] = $contents[1]["city"];
          $response["con_title_2"] = $contents[1]["con_title"];
          $response["con_data1_2"]=$contents[1]["con_data1"];
          $response["con_data2_2"] = $contents[1]["con_data2"];
          $response["con_data3_2"]=$contents[1]["con_data3"];
          $response["con_data4_2"]=$contents[1]["con_data4"];
          $response["con_photo_2"]=$contents[1]["con_photo"];
          $response["created_at_2"]=$contents[1]["created_at"];
          $response["recommend_2"] =$contents[1]["recommend"];

        	echo json_encode($response);
	}else {
		$response["error"] = TRUE;
		$response["error_msg"] = "can't bring the list of user who put recomendation";
        	echo json_encode($response);
	}

?>
