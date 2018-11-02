<?php

//include("/var/www/html/android_login_api/gcm-push-module.php");
require_once 'include/DB_Functions.php';
$db = new DB_Functions();

$apiKey = 'AIzaSyDMBUsCelMBGWWg6tV0fTsa1uhLlkIlj6s';
$title = '미리여행';
$extra=1;
$message = '알림이 있습니다.';

 $writer=$_GET['writer'];
 $type=$_GET['type'];
 if($type==1){
	$message='내 글이 추천되었습니다.';
 }else{
	$message='나를 팔로우한 사람이 있습니다.';
 }
 $data = $db->getScrapList($writer);
 $gcm_token= $data["token"];
//$gcm_token = 'dnV_3ar_Oi4:APA91bE8-Nb9Mk34CWUDMmoqXMpj5uZCDbiXU-WoxU7W0071epxM7ZnW1-WvmSIdO55VgtN9ZKjYJ-x0GBA9tXZR1L7p_Lnrh-hQokZluzZnAP1sEAkjZfTSpsLCZ7xxgH7uCmXl-cYT';
$pusher = $db->GCMPushMessage($apiKey, true);
$db->setDevices($gcm_token);
$response = $db->send($title, $message, $extra);

print_r($response);
?>
