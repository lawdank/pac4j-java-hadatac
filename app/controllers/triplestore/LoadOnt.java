package controllers.triplestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import controllers.routes;
import play.mvc.*;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http.MultipartFormData.FilePart;

import javax.inject.Inject;

//import org.apache.commons.io.IOutil;
//import controllers.AuthApplication;
//import views.html.triplestore.*;
import metadata.loader.MetadataContext;
import util.ConfigProp;
import util.Feedback;
import util.NameSpace;
import util.NameSpaces;

import com.typesafe.config.ConfigFactory;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import views.html.triplestore.loadOnt;


public class LoadOnt extends Controller {

    public static final String LAST_LOADED_NAMESPACE = "/last-loaded-namespaces-properties";

    @Inject
    private FormFactory formFactory;

    //    @Restrict(@Group(AuthApplication.DATA_MANAGER_ROLE))
    public Result loadOnt(String oper) {

        List<String> cacheList = new ArrayList<String>();
        File folder = new File(NameSpaces.CACHE_PATH);

        // if the directory does not exist, create it
        if (!folder.exists()) {
            System.out.println("creating directory: " + NameSpaces.CACHE_PATH);
            try{
                folder.mkdir();
            } catch(SecurityException se){
                System.out.println("Failed to create directory.");
            }
            System.out.println("DIR created");
        }

        String name = "";
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                name = fileEntry.getName();
                if (name.startsWith(NameSpaces.CACHE_PREFIX)) {
                    name = name.substring(NameSpaces.CACHE_PREFIX.length());
                    cacheList.add(name);
                }
            }
        }

        return ok(loadOnt.render(oper, cacheList, NameSpaces.getInstance().getOrderedNamespacesAsList()));
    }

    //    @Restrict(@Group(AuthApplication.DATA_MANAGER_ROLE))
    public Result postLoadOnt(String oper) {
        return loadOnt(oper);
    }

    public static String playLoadOntologies(String oper) {
        NameSpaces.getInstance();
        MetadataContext metadata = new
                MetadataContext("user",
                "password",
                ConfigFactory.load().getString("hadatac.solr.triplestore"),
                false);
        return metadata.loadOntologies(Feedback.WEB, oper);
    }

    //    @Restrict(@Group(AuthApplication.DATA_MANAGER_ROLE))
    public Result eraseCache() {
        List<String> cacheList = new ArrayList<String>();
        File folder = new File(NameSpaces.CACHE_PATH);
        String name = "";

        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                name = fileEntry.getName();
                if (name.startsWith(NameSpaces.CACHE_PREFIX)) {
                    fileEntry.delete();
                }
            }
        }

        return ok(loadOnt.render("init", cacheList, NameSpaces.getInstance().getOrderedNamespacesAsList()));
    }

    //    @Restrict(@Group(AuthApplication.DATA_MANAGER_ROLE))
//    public Result reloadNamedGraphFromRemote(String abbreviation) {
//        NameSpace ns = NameSpaces.getInstance().getNamespaces().get(abbreviation);
//        ns.deleteTriples();
//
//        String url = ns.getURL();
//        if (!url.isEmpty()) {
//            ns.loadTriples(url, true);
//        }
//        ns.updateLoadedTripleSize();
//
//        return redirect(routes.LoadOnt.loadOnt("init"));
//    }

    //    @Restrict(@Group(AuthApplication.DATA_MANAGER_ROLE))
//    public Result reloadNamedGraphFromCache(String abbreviation) {
//        NameSpace ns = NameSpaces.getInstance().getNamespaces().get(abbreviation);
//        ns.deleteTriples();
//
//        String filePath = NameSpaces.CACHE_PATH + "copy" + "-" + ns.getAbbreviation().replace(":", "");
//        File localFile = new File(filePath);
//        if (localFile.exists()) {
//            ns.loadTriples(filePath, false);
//            ns.updateLoadedTripleSize();
//        } else {
//            return badRequest("No cache for this namespace!");
//        }
//
//        return redirect(routes.LoadOnt.loadOnt("init"));
//    }

    //    @Restrict(@Group(AuthApplication.DATA_MANAGER_ROLE))
//    public Result deleteNamedGraph(String abbreviation) {
//        NameSpace ns = NameSpaces.getInstance().getNamespaces().get(abbreviation);
//        ns.deleteTriples();
//        ns.updateLoadedTripleSize();
//
//        return redirect(routes.LoadOnt.loadOnt("init"));
//    }

    //    @Restrict(@Group(AuthApplication.DATA_MANAGER_ROLE))
//    public Result deleteAllNamedGraphs() {
//        for (NameSpace ns : NameSpaces.getInstance().getNamespaces().values()) {
//            ns.deleteTriples();
//            ns.updateLoadedTripleSize();
//        }
//
//        return redirect(routes.LoadOnt.loadOnt("init"));
//    }

    @SuppressWarnings("unchecked")
//    @Restrict(@Group(AuthApplication.DATA_MANAGER_ROLE))
//    public Result saveNamespaces(Http.Request request) {
//        int original_size = NameSpace.findAll().size();
//
//        Form form = formFactory.form().bindFromRequest(request);
//        Map<String, String> data = form.rawData();
//
//        NameSpace.deleteAll();
//        for (int i = 0; i < Math.max(original_size, data.size() / 3); ++i) {
//            if (!data.containsKey("nsAbbrev" + String.valueOf(i + 1))) {
//                continue;
//            }
//
//            NameSpace ns = new NameSpace();
//            ns.setAbbreviation(data.get("nsAbbrev" + String.valueOf(i + 1)));
//            ns.setName(data.get("nsName" + String.valueOf(i + 1)));
//            ns.setType(data.get("nsType" + String.valueOf(i + 1)));
//            ns.setURL(data.get("nsURL" + String.valueOf(i + 1)));
//            ns.save();
//        }
//        NameSpaces.getInstance().reload();
//
//        return redirect(routes.LoadOnt.loadOnt("init"));
//    }

    //    @Restrict(@Group(AuthApplication.DATA_MANAGER_ROLE))
//    @BodyParser.Of(value = BodyParser.MultipartFormData.class)
//    public Result importNamespaces(Http.Request request) {
//        System.out.println("importNamespaces is called");
//        String name_last_loaded_namespace = "";
//        FilePart uploadedfile = request.body().asMultipartFormData().getFile("ttl");
//        if (uploadedfile != null) {
//            File file = (File)uploadedfile.getRef();
//            name_last_loaded_namespace = uploadedfile.getFilename();
//            FileInputStream inputStream;
//            try {
//                inputStream = new FileInputStream(file);
//                List<NameSpace> namespaces = NameSpaces.loadFromFile(inputStream);
//                inputStream.close();
//
//                NameSpace.deleteAll();
//                for (NameSpace ns : namespaces) {
//                    ns.save();
//                }
//            } catch (Exception e) {
//                return badRequest("Could not find uploaded file");
//            }
//        } else {
//            return badRequest("Error uploading file. Please try again.");
//        }
//        NameSpaces.getInstance().reload();
//
//        // save the name of the last uploaded namespace
//        File lastloadedfile = new File(ConfigProp.getPathDownload() + LAST_LOADED_NAMESPACE);
//        try {
//            FileOutputStream lastLoadedOutputStream = new FileOutputStream(lastloadedfile);
//            System.out.println("Name last loaded prop file: " + lastLoadedOutputStream);
//            lastLoadedOutputStream.write(name_last_loaded_namespace.getBytes());
//            lastLoadedOutputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return redirect(routes.LoadOnt.loadOnt("init"));
//    }

    public static String getNameLastLoadedNamespace() {
        File lastloadedfile = new File(ConfigProp.getPathDownload() + LAST_LOADED_NAMESPACE);
        String name_last_loaded_namespace = "";
        try {
            FileInputStream inputStream = new FileInputStream(lastloadedfile);
//            name_last_loaded_namespace = IOutil.toString(inputStream, StandardCharsets.UTF_8);
            inputStream.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return name_last_loaded_namespace;
    }


    //    @Restrict(@Group(AuthApplication.DATA_MANAGER_ROLE))
    public Result exportNamespaces() {
        String path = ConfigProp.getPathDownload();
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(path + "/namespaces.properties");
        try {
            List<String> lines = new ArrayList<String>();

            for (NameSpace ns : NameSpaces.getInstance().getOrderedNamespacesAsList()) {
                lines.add(ns.getAbbreviation() + "=" + String.join(",", Arrays.asList(
                        ns.getName(), ns.getType(), ns.getURL())));
            }

            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(String.join("\n", lines).getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ok(file);
    }
}
