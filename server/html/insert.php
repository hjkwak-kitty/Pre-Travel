<html>
<head> 

<title>PHP 게시판 프로젝트 - 쓰기</title> 

 

<STYLE TYPE="text/css"> 

BODY,TD,SELECT,input,DIV,form,TEXTAREA,center,option,pre,blockquote {font-family:굴림;font-size:9pt;color:#555555;} 

A:link {color:black;text-decoration:none;} 

A:visited {color:black;text-decoration:none;} 

A:active {color:black;text-decoration:none;} 

A:hover {color:gray;text-decoration:none;} 

</STYLE> 

 

 

<script language="javascript"> 

 

function check_submit() { 

        document.myForm.action = "insert.php"; 

        document.myForm.submit(); 

} 

 

</script> 

 

</head> 

 

<body bgcolor=white> 

<br> 

<form name='myForm' method='post' ENCTYPE='multipart/form-data'> 

<!-- 파일 첨부를 위해 ENCTYPE='multipart/form-data'를 추가함 --> 

 

<table border=0 cellspacing=1 cellpadding=0 width=670> 

    <tr> 

     <td align=center> 

     <font color=green><b>글 쓰기 화면입니다.</b></font> 

     </td> 

    </tr> 

</table> 

 

<table border=0 bgcolor=#CCCCF><tr><td> 

 

<table border=0 width=670 cellspacing=0 cellpadding=0 bgcolor=#F0F0F0> 

 

    <col width=100></col><col width=></col> 

 

    <tr> 

     <td colspan=2> 

     <table border=0 cellspacing=0 cellpadding=0 width=100%> 

           <tr> 

           <td width=100 align=right><b>이름&nbsp;</b></td> 

          <td><input type=text name=name size=20 maxlength=20></td>           

          <td width=100 align=right><b>비밀번호&nbsp;</b></td> 

          <td><input type=password name=password size=20 maxlength=20></td> 

          </tr> 

            </table> 

     </td> 

    </tr> 

 

    <tr><td bgcolor=white height=1 colspan=2></td></tr> 

     

    <tr> 

     <td align=right><b>전자우편&nbsp;</b></td> 

     <td> <input type=text name=email size=40 maxlength=200> </td> 

    </tr> 

     

    <tr><td bgcolor=white height=1 colspan=2></td></tr> 

 

    <tr> 

     <td align=right><b>홈페이지&nbsp;</b></td> 

     <td> <input type=text name=homepage size=40 maxlength=200> </td> 

    </tr> 

 

    <tr><td bgcolor=white height=1 colspan=2></td></tr> 

 

    <tr> 

     <td align=right><b>제목&nbsp;</b></td> 

     <td> <input type=text name=subject size=87 maxlength=200> </td> 

    </tr> 

 

    <tr><td bgcolor=white height=1 colspan=2></td></tr> 

 

    <tr> 

     <td align=right><b>내용&nbsp;</b></td> 

     <td valign=top> 

     <textarea name=memo cols=85 rows=20></textarea> 

     </td> 

    </tr> 

 

</table> 

 

<br> 

 

<table border=0 width=670> 

<!-- 파일 첨부를 위해 추가한 부분 : 시작 --> 

    <tr> 

        <td align=center>파일첨부 : <input type="file" name="upfile" size="20"></td> 

    </tr> 

<!-- 파일 첨부를 위해 추가한 부분 : 끝--> 

<tr><td> 

<center> 

<a href="javascript:check_submit();">입력</a>  

<a href="list.php">리스트</a> 

</center> 

</td></tr> 

</table> 

</td></tr></table> 

 

</form> 

 

</body> 

</html> 
