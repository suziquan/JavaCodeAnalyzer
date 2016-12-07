<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Issue report</title>
  <link href="sonarlintreport_files/sonar.css" media="all" rel="stylesheet" type="text/css">
  <script type="text/javascript" src="sonarlintreport_files/jquery.min.js"></script>
	<style>
	html {
  color: #111;
  background: #FFF;
}
body,div,dl,dt,dd,ul,ol,li,h1,h2,h3,h4,h5,h6,pre,code,form,fieldset,legend,input,textarea,p,blockquote,th,td{margin:0;padding:0;}
body {
  font: 13px/1.231 arial, helvetica, clean, sans-serif;
  padding-left:40px;
  padding-right:40px;
}
#content {
  padding: 5px;
}

#rules {
  margin-bottom: 25px;
}
#rules a, #rules a:visited {
  color: #111;
}
#rules .ruleSubtitle, #rules .ruleSubtitle a, #rules .ruleSubtitle a:visited {
  font-size: 93%;
  color: #777;
}
.globalIssues {
  width: 100%;
  border: 0;
  margin-left: 37px;
}

hr {
  border: none;
  border-top: 2px dashed #DDD;
  color: #FFF;
  height: 10px;
}
div.banner {
  height: 26px;
  line-height: 26px;
  background-color: #EEEEEE;
  border: 1px solid #DDDDDD;
  color: #444;
  font-size: 85%;
  margin-bottom: 10px;
  padding: 0 5px;
}
table.data > thead > tr > th {
  font-size: 93%;
  padding: 4px 7px 4px 3px;
  font-weight: normal;
}
table.data > tfoot > tr > td {
  font-size: 93%;
  color: #777;
  padding: 4px 0 4px 10px;
}

table.data > tbody > tr > td {
  padding: 4px 7px 4px 3px;
  vertical-align: text-top;
}

table.data td.small, table.data th.small {
  padding: 0;
  white-space: nowrap;
}

table.data th img, table.data td img {
  vertical-align: text-bottom;
}

.data thead tr.total {
  background-color: #ECECEC;
  font-weight: normal;
  border: 1px solid #DDD;
}

.data thead tr.total th {
  font-weight: normal;
}

table.data > thead {
  border-bottom: 1px solid #ddd;
}

table.data > tbody {
  border-bottom: 1px solid #ddd;
  border-right: 1px solid #ddd;
  border-left: 1px solid #ddd;
}

table.data, table.spaced, .gwt-SourcePanel .sources {
  width: 100%;
}

table.data>thead>tr>th {
  font-size: 93%;
  padding: 4px 7px 4px 3px;
}

table.data>tfoot>tr>td {
  font-size: 93%;
  color: #777;
  padding: 4px 0 4px 10px;
}

table.data>tbody>tr>td {
  padding: 4px 7px 4px 3px;
  vertical-align: text-top;
}

.data thead tr.total {
  background-color: #ECECEC;
}

.data thead tr.total th {
  font-weight: bold;
}

.hoverable:hover {
  background-color: #f7f7f7;
}

div, ul, li, h2, pre, form, input, td {
  margin: 0;
  padding: 0;
}

table {
  border-collapse: collapse;
  border-spacing: 0;
}

img {
  border: 0;
}

select, input, textarea {
  font: 99% arial, helvetica, clean, sans-serif;
}

pre, code {
  font-family: monospace;
  line-height: 100%;
}

code {
  font-size: 93%;
}

em {
  font-weight: bold;
}

h1 {
  color: #444;
  font-size: 16px;
}

h2 {
  color: #2B547D;
  font-size: 16px;
  font-weight: normal;
}

h3 {
  font-size: 100%;
  font-weight: bold;
}

h4 {
  font-size: 85%;
  color: #777;
}

div.issue {
  background-color: #FFF;
  border: 1px solid #DDD;
  margin: 7px;
}


.file_title {
  line-height: 1.5em;
  height: 1.5em;
  font-size: 1.2em;
  margin: 10px 0 5px 0;
  vertical-align: middle;
}

span.better {
    color: green;
}
span.worst {
    color: red;
}

h1{
	padding-top:15px;
	padding-bottom:5px;
}

	</style>
  </head>
<body>
<div id="content">

<div>
<h1>Global Issues</h1>
<div>
<#list globalIssues as globalIssue>
<div style="padding:5px;">${globalIssue}</div>
</#list>
</div>
<br/>
<h1>Issues Per File</h1>
<#list issuesMap?keys as filePath>
  <div style="margin-bottom:5px;">
  <table width="100%" class="data">
    <thead>
    <tr class="total">
      <th align="left" colspan="2" nowrap>
        <div class="file_title">
        File: 
          <a href="#" style="color: black">${filePath}</a>
        </div>
      </th>
      <th align="right" width="1%" nowrap>
          <span class="worst" id="total">${issuesMap["${filePath}"]?size}</span>
        Issues
      </th>
    </tr>
    </thead>
    <tbody>
    <#list issuesMap["${filePath}"] as issue>
      <tr>
        <td width="60">
         <em> Line  		${issue.lineNumber} :</em>
        </td>
        <td align="left">
		${issue.message}
        </td>
        <td align="right">
        </td>
      </tr>
	</#list>
    </tbody>
  </table>
  </div>
  </#list>
</div>
</body>
</html>
