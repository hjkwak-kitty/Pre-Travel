<?php
require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

$email = "hjkwak91@gmail.com";
 $mailto = $email;


        $temp = substr($email,0,10);
        $origin="hi_". $temp."_bye";
	echo $mailto;


        // get the user by email and password
        $middle = $db->getAuthText($origin);
        if ($middle != false) {
		echo $origin;
		$compare = $db->checkAuth($origin,$middle["salt"]);
		$compareText = substr($compare["encrypted"],0,6);
                $finalText = substr($middle["encrypted"],0,6);
                        $response["error"]= FALSE;
                        $response["salt"]= $middle["salt"];
			$response["salt2"]= $compare["salt"];
                        if($finalText=$compareText){
			echo json_encode($response);}
				
        }else{
                //인증문자생성실패
                $response["error"] = TRUE;
                $response["error_msg"] = "인증문자 생성 실패.";
                echo json_encode($response);
        }



?>
