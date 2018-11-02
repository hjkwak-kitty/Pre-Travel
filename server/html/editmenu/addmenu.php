<html>
<head><title> add menu </title></head>

<body>

<form name="edit" method="POST" action="editmenu.php">

<?php
$connect = mysql_connect("localhost","root","letmesee55!");
$db=mysql_select_db("album",$connect);

$new= $_POST["new"];
$dir= "/var/www/html/img/".$new;

$getID="select id from album where id='$new'";
$getID=mysql_query($getID);
$getID=mysql_fetch_array($getID);

if($getID['id']){
echo "이미 같은 이름의 앨범이 있습니다. ";
}
else{
if(!is_file($dir)){
echo "폴더 생성, ";
echo exec("mkdir $dir");
exec("chmod 707 $dir");
}
$sql="insert into album values('$new','0','$dir')";
mysql_query($sql,$connect);

}

if($sql&&!($getID['id'])) echo "Input Success<br>";
else echo "Input Failed<br>";
echo "<hr>";
?>
<input type="submit" value="Back">

</form>
</body>
</html>
