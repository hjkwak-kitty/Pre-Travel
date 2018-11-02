
<?php
require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['user_name'])&&isset($_POST['token'])&&isset($_POST['gcm'])) {

         // receiving the post params
        $user_name = $_POST['user_name'];
	$token=$_POST['token']; //��ū��ȣ
        $gcm = $_POST['gcm']; //�˶� �¿��� ����

        if ($db->alramSetting($user_name, $gcm, $token)) {
        	 $response["error"] = FALSE;
             	   //$response["recommend"] = TRUE;
                   echo json_encode($response);
             
	}else {
                $response["error"] = TRUE;
                $response["error_msg"] = "����";
                echo json_encode($response);
        }
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameter is missing!";
    echo json_encode($response);
}
?>
