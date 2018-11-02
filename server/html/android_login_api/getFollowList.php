<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['user_name'])&&isset($_POST['writer'])) {
         // receiving the post params
        $user_name = $_POST['user_name'];
	$writer = $_POST['writer'];
        $followList = $db->getScrapList($writer);
	$followingList= $db->getScrapList($user_name);
        if ($followList&&$followingList) {
                // user already existed
                $response["error"] = FALSE;
                $response["following"] = $followList["follower"];//내가 팔로우한 사람 리스트
                $response["follow"] = $followingList["follow"];//내가 팔로우한 사람의 팔로우리스트
                echo json_encode($response);
        }else {
                $response["error"] = TRUE;
                $response["error_msg"] = "can't bring the follow List";
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

