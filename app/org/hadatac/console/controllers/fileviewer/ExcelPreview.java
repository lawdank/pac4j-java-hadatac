package org.hadatac.console.controllers.fileviewer;

//import org.hadatac.console.controllers.AuthApplication;
import org.hadatac.console.models.SysUser;
import org.hadatac.console.views.html.fileviewer.*;
import org.hadatac.entity.pojo.DataFile;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import play.mvc.Controller;
import play.mvc.Result;


public class ExcelPreview extends Controller {
    
//    @Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
    public Result index(String fileId, boolean bSavable) {
//        final SysUser user = AuthApplication.getLocalUser(session());
        DataFile dataFile = DataFile.findById(fileId);
        if (null == dataFile) {
            return ok(excel_preview.render(dataFile, false));
        }
        
//        dataFile.updatePermissionByUserEmail(user.getEmail());
        
        if (dataFile.getAllowEditing()) {
            return ok(excel_preview.render(dataFile, bSavable));
        }
        
        if (dataFile.getAllowViewing()) {
            return ok(excel_preview.render(dataFile, false));
        }
        
        return badRequest("No perview permission!");
    }
    
//    @Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
    public Result postIndex(String fileId, boolean bSavable) {
        return index(fileId, bSavable);
    }
    
//    @Restrict(@Group(AuthApplication.FILE_VIEWER_EDITOR_ROLE))
    public Result fromViewableLink(String viewableId) {
        DataFile dataFile = DataFile.findByViewableId(viewableId);
        if (null == dataFile) {
            return badRequest("Invalid link!");
        }
        
//        final SysUser user = AuthApplication.getLocalUser(session());
//        if (!user.isDataManager() && !dataFile.getViewerEmails().contains(user.getEmail())) {
//            return badRequest("You don't have permission to view this file!");
//        }
        
        return ok(excel_preview.render(dataFile, false));
    }
    
//    @Restrict(@Group(AuthApplication.FILE_VIEWER_EDITOR_ROLE))
    public Result postFromViewableLink(String viewableId) {
        return fromViewableLink(viewableId);
    }
    
//    @Restrict(@Group(AuthApplication.FILE_VIEWER_EDITOR_ROLE))
    public Result fromEditableLink(String editableId) {
        DataFile dataFile = DataFile.findByViewableId(editableId);
        if (null == dataFile) {
            return badRequest("Invalid link!");
        }
        
        return ok(excel_preview.render(dataFile, false));
    }
    
//    @Restrict(@Group(AuthApplication.FILE_VIEWER_EDITOR_ROLE))
    public Result postFromEditableLink(String editableId) {
        return fromEditableLink(editableId);
    }
}

