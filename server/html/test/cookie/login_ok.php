<?php

if(!isset($_POST['user_id']) || !isset($_POST['user_pw'])) exit;
$user_id = $_POST['user_id'];
$user_pw = $_POST['user_pw'];
$members = array('admin2'=>array('pw'=>'1234', 'name'=>'효진'));
if(!isset($members[$user_id])) {
	echo "<script>alert('패스워드가 잘못되었습니다.');history.back();</script>";
	exit;
}
if($members[$user_id]['pw'] != $user_pw) {
	echo "<script>alert('패스워드가 잘못되었습니다.');history.back();</script>";
	exit;
}
setcookie('user_id',$user_id,time()+(86400*30),'/');
setcookie('user_name',$members[$user_id]['name'],time()+(86400*30),'/');
?>
<meta http-equiv='refresh' content='0;url=../index.php'>
