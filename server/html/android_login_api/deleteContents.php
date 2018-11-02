<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if(isset($_POST['text_uid'])){
 
  $text_uid = $_POST['text_uid'];

  $delete = $db->deleteContent($text_uid);
  if($delete){
        $response["error"]=FALSE;
        echo json_encode($response);
  }else{
        $response["error"]=TRUE;
        $response["error_msg"]="실패!";
        echo json_encode($response);
  }

}else{
  $response["error"]=TRUE;
  $response["error_msg"] = "Required parameters is missing!";
  echo json_encode($response);
}
?>


