<html>
<body>
<?php

 $connect = mysql_connect("localhost","root","letmesee55!");
 $db=mysql_select_db("album",$connect);
 $sql ="select * from album";
 $sql_result=mysql_query($sql,$connect);
 $count = mysql_num_rows($sql_result);
 $where = $_COOKIE[country];
 $country= $_GET[country];
 $title=$_GET[title]; 
 
 session_start();
        if(!isset($_SESSION['user_id']) || !isset($_SESSION['user_name'])){
        echo "<meta http-equiv='refresh' content='0;url=login.php'>";
        exit;
        }
        $user_id = $_SESSION['user_id'];
        $user_name= $_SESSION['user_name'];
        echo "<p>안녕하세요. $user_name($user_id)님</p>";
	echo "<a href='editmenu/logout.php'>로그아웃</a><hr><br>";

?>
<form enctype="multipart/form-data" method="POST" action="update.php">

        <?php echo "<b>Album &nbsp;	</b>"; ?>

        <select id="selectCountry" name="country" onchange="setValues();">
        <option value=<?php echo $where; ?> selected><?php echo $where; ?> </option>	
	<?php
	echo("<option value='{$where}' selected>{$where}</option>");
        for($i=0;$i<$count;$i++){
        $image_num=mysql_result($sql_result,$i,image_num);
        $id=mysql_result($sql_result,$i,id);
        echo("<option value='{$id}'>{$id}</option>");
        }?>
        </select>
        
	<br />
        <tr><td align=right><b> Photo(jpg, png) &nbsp;</b></td>	
	<input name="file" type="file" id="file" />

	<br />
	<tr><td bgcolor=white height=1 colspan=2></td></tr>    

	<tr>
     	<td align=right><b>Title &nbsp;</b></td>
     	<td> <input type=text name=name size=70 maxlength=200> </td>
	</tr>
	
	<br />
	<tr><td bgcolor=white height=1 colspan=2></td></tr>
      
	<tr> 
	<td align=right><b> MEMO &nbsp;</b></td>
	<td valign=top>
     	<textarea name='memo' cols='50' rows='20'></textarea>
     	</td>
    	</tr>

 <br />
 <br />
    <input type="submit" value="update" />
</form>
</body>
<html>
