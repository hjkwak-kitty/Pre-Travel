


<?php
require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['country'])&&isset($_POST['city'])){
    // receiving the post params
    $country = $_POST['country'];
    $city = $_POST['city'];

    // get the user by email and password
    $add = $db->storeWhere($country, $city);
 $response["error"] = FALSE;
echo json_encode($response);

}else{
  // required post params is missing
   $response["error"] = TRUE;
   $response["error_msg"] = "Required parameter is  missing!";
   echo json_encode($response);
}

