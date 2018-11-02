
<?php
require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['user_name'])&&isset($_POST['user_follow'])&&isset($_POST['writer'])&&isset($_POST['write_follower'])) {

         // receiving the post params
        $user_name = $_POST['user_name'];
	$writer=$_POST['writer'];
        $user_follow = $_POST['user_follow'];
        $write_follower = $_POST['write_follower'];

        if ($db->pushFollow($writer, $write_follower)) {
        	if ($db->pushFollower($user_name, $user_follow)) {
                   $response["error"] = FALSE;
             	   //$response["recommend"] = TRUE;
                   echo json_encode($response);
        	}else {
                 $response["error"] = TRUE;
               	 $response["error_msg"] = "실패";
               	 echo json_encode($response);
        	}        
	}else {
                $response["error"] = TRUE;
                $response["error_msg"] = "실패";
                echo json_encode($response);
        }
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameter is missing!";
    echo json_encode($response);
}
?>
