/**
* @output           false
* @mappedSuperClass true
* @hint             I define common properties/methods and can be extended by other ORM component
*/
component
{
    // log defaults per table (handled by ormEventhandler)
    property name="createdOn"   ormtype="timestamp" notnull="true";
    property name="updatedOn"   ormtype="timestamp";

    private string function cleanEmoji(required string value){
        local.gotourl   = "http" & (application.useSSL ? "s" : "") & "://" & application.app_domain & "/clean-emoji.php";
        local.php       = new http(url:gotourl,method:"post");
        local.php.addParam(type:"formfield",name:"value",value:arguments.value);
        return trim(local.php.send().getPrefix().fileContent);
    }
}
