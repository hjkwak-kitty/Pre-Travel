<html>
<?php
$name = $_POST['name'];
$country = $_POST['country'];
$memo =$_POST['memo'];
$where = "img/".$country."/";

$connect = mysql_connect("localhost","root","letmesee55!");
$db=mysql_select_db("album",$connect);


if(($_FILES["file"]["type"] == "image/jpg")||($_FILES["file"]["type"] == "image/gif")||($_FILES["file"]["type"] == "image/jpeg")||($_FILES["file"]["type"] == "image/png")||($_FILES["file"]["type"] == "image/pjpeg")){
	if($_FILES["file"]["error"] >0){ 
		echo "Error: ".$FILES["file"]["error"]."<br />";}
	else{
		echo "Upload: ".$_FILES["file"]["name"]."<br />";
		echo "Type: ".$_FILES["file"]["type"]."<br />";
		echo "Size: ".$_FILES["file"]["size"]."<br />";
		echo "Stored in: ".$_FILES["file"]["tmp_name"]."<br />";
	 	if(file_exists("img/etc/".$_FILES["file"]["name"])){
		 echo $_FILES["file"]["name"]."이 이미 존재합니다.";
		}
		else{
		 move_uploaded_file($_FILES["file"]["tmp_name"], $where.$name.".jpg");
		 echo "Stored in: ".$where;
		 
		 $sql="insert into photo values('$country','$name','$memo', '0')";
		 mysql_query($sql,$connect);
		


		}
	} 
}
else{
	echo $_FILES["file"]["type"];
	echo "이미지 파일이 아닙니다.";
}
?>
<form action=<?php echo "list.php?country=".$country; ?> method="POST">
<input type='submit' value='완료'/>
</form>
</html>
