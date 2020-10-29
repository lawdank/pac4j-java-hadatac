package controllers.sandbox;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
import play.mvc.*;
//import play.mvc.Result;
//
//import org.apache.commons.lang3.time.DateUtils;
//import controllers.AuthApplication;
//import org.hadatac.console.models.SysUser;
//import controllers.routes;
//import views.html.sandbox.*;
//import org.hadatac.entity.pojo.OperationMode;
//import util.NameSpaces;
//
//import be.objectify.deadbolt.java.actions.Group;
//import be.objectify.deadbolt.java.actions.Restrict;
//
//
public class Sandbox extends Controller {

    public static final String SUFFIX = "_sandbox";
//
//    @Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
//    public Result index(Http.Request request) {
//        final SysUser user = AuthApplication.getLocalUser(request.session());
//
//        List<OperationMode> modes = OperationMode.findAll();
//        if (modes.size() == 0) {
//            return ok(sandbox.render(true, "", OperationMode.REGULAR,
//                    "Sandbox mode is OFF. You are allowed to open the SANDBOX mode now.",
//                    routes.Portal.index().url()));
//        } else if (modes.size() == 1) {
//            OperationMode mode = modes.get(0);
//            if (mode.getUserEmail().equals(user.getEmail())) {
//                if (mode.getOperationMode().equals(OperationMode.SANDBOX)) {
//                    return ok(sandbox.render(true, mode.getUserEmail(), OperationMode.SANDBOX,
//                            "Sandbox mode is ON. You are allowed to exit the SANDBOX mode now.",
//                            routes.Portal.index().url()));
//                }
//            } else {
//                return ok(sandbox.render(false, mode.getUserEmail(), OperationMode.SANDBOX,
//                        "Another user " + mode.getUserEmail() + " is using the SANDBOX mode. You are NOT allowed to operate now.",
//                        routes.Portal.index().url()));
//            }
//        }
//
//        return badRequest("Internal errors occurred for Sandbox Management!");
//    }
//
//    @Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
//    public Result postIndex(Http.Request request) {
//        return index(request);
//    }
//
//    @Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
//    public Result startSandboxMode(Http.Request request) {
//        final SysUser user = AuthApplication.getLocalUser(request.session());
//
//        List<OperationMode> modes = OperationMode.findAll();
//        if (modes.size() == 0) {
//            OperationMode mode = new OperationMode();
//            mode.setUserEmail(user.getEmail());
//            mode.setOperationMode(OperationMode.SANDBOX);
//            mode.setLastEnterTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
//            mode.save();
//
//            NameSpaces.getInstance().reload();
//        }
//
//        return redirect(controllers.sandbox.routes.Sandbox.index());
//    }
//
//    @Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
//    public Result exitSandboxMode(Http.Request request) {
//        final SysUser user = AuthApplication.getLocalUser(request.session());
//
//        OperationMode mode = OperationMode.findByEmail(user.getEmail());
//        if (mode != null) {
//            mode.delete();
//
//            NameSpaces.getInstance().reload();
//        }
//
//        return redirect(controllers.sandbox.routes.Sandbox.index());
//    }
//
//    public static void checkSandboxExpiration() {
//        List<OperationMode> modes = OperationMode.findAll();
//        if (modes.size() > 0) {
//            OperationMode mode = modes.get(0);
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//            try {
//                Date enterTime = sdf.parse(mode.getLastEnterTime());
//                Date expirationTime = DateUtils.addHours(enterTime, 2);
//                Date currentTime = new Date();
//                if (currentTime.after(expirationTime)) {
//                    mode.delete();
//                }
//            } catch (ParseException e) {
//                System.out.println("Cannot parse enter time!");
//            }
//        }
//    }
}
