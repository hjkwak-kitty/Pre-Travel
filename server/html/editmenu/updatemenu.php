<html>
<head><title> update menu </title></head>

<body>

<form name="edit" method="POST" action="editmenu.php">

<?php
$connect = mysql_connect("localhost","root","letmesee55!");
$db=mysql_select_db("album",$connect);

$country= $_POST["country"];
$new= $_POST["new"];
$dir_old = "/var/www/html/img/".$country;
$dir_new = "/var/www/html/img/".$new;

$getID="select id from album where id='$new'";
$getID=mysql_query($getID);
$getID=mysql_fetch_array($getID);

if($getID['id']){
echo "이미 같은 이름의 앨범이 있습니다. ";
}
else{
exec("mv $dir_old $dir_new");
exec("chmod 707 $dir_new");
$sql="update album set directory='$dir_new' where id='$country'";
mysql_query($sql,$connect);
$sql="update album set id='$new' where id='$country'";
mysql_query($sql,$connect);
$sql="update photo set album='$new' where album='$country'";
mysql_query($sql,$connect);

}

if($sql) echo "Update Success<br>";
else echo "Update Failed<br>";
echo "<hr>";
?>
<input type="submit" value="Back">

</form>
</body>
</html>
