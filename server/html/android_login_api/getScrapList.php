<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['user_name'])) {	
	 // receiving the post params
	$user_name = $_POST['user_name'];
	$scrapList = $db->getScrapList($user_name);
	//echo $recommendList['recommendList'];
	if ($scrapList) {
		// user already existed
        	$response["error"] = FALSE;
        	$response["scrap_uid"] = $scrapList["scrap_uid"];
		$response["scrap_title"] = $scrapList["scrap_title"];
        	echo json_encode($response);
	}else {
		$response["error"] = TRUE;
		$response["error_msg"] = "can't bring the scrap List";
		//$response["recommendList"] = FALSE;
        	echo json_encode($response);
	}
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters email is missing!";
    echo json_encode($response);
}
?>
