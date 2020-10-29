package controllers.dataacquisitionsearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import play.mvc.*;
import play.mvc.Http.MultipartFormData.FilePart;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import views.html.dataacquisitionsearch.loadCCSV;
import org.hadatac.data.loader.util.Arguments;
import org.hadatac.data.loader.util.FileFactory;
import org.hadatac.data.model.ParsingResult;

public class LoadCCSV extends Controller {

    static FileFactory files;

    public static final String UPLOAD_NAME = "uploads/latest.ccsv";

    public Result loadCCSV(String oper) {
        return ok(loadCCSV.render(oper, ""));
    }

    public Result postLoadCCSV(String oper) {
        return ok(loadCCSV.render(oper, ""));
    }

    public static ParsingResult playLoadCCSV() {
        int status = 0;
        String message = "";
        Arguments arguments = new Arguments();
        arguments.setInputPath(UPLOAD_NAME);
        arguments.setInputType("CCSV");
        arguments.setOutputPath("upload/");
        arguments.setVerbose(true);
        arguments.setPv(false);

        File inputFile = new File(arguments.getInputPath());
        files = new FileFactory(arguments);
        files.setCCSVFile(inputFile, inputFile.getName());

        try {
            files.openFile("log", "w");
            files.writeln("log", "[START] " + arguments.getInputPath() + " generating measurements.");

			/*   if (arguments.getInputType().equals("CCSV")) {
		Parser parser = new Parser();
		if (arguments.isPv()) {
		    ParsingResult result = parser.validate(Feedback.WEB, files);
		    status = result.getStatus();
		    message += result.getMessage();
		} else {
		    ParsingResult result = parser.validate(Feedback.WEB, files);
		    status = result.getStatus();
		    message += result.getMessage();
		    if (status == 0) {
			ParsingResult result_parse = parser.index(Feedback.WEB);
			status = result_parse.getStatus();
			message += result_parse.getMessage();
		    }
		}
		} */

            files.writeln("log", "[END] " + arguments.getInputPath() + " generating measurements.");
            files.closeFile("log", "w");
        } catch (Exception e) {
            e.printStackTrace();
            status = 1;
            message += "Exception in playLoadCCSV(): ";
            message += e.toString();
        }

        message += "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "] end of file";
        return new ParsingResult(status, message);
    }

    @BodyParser.Of(value = BodyParser.MultipartFormData.class)
    public Result uploadFile(Http.Request request) {
        FilePart uploadedfile = request.body().asMultipartFormData().getFile("pic");
        if (uploadedfile != null) {
            File file = (File)uploadedfile.getRef();
            File newFile = new File(UPLOAD_NAME);
            InputStream isFile;
            try {
                isFile = new FileInputStream(file);
                byte[] byteFile;
                try {
                    byteFile = IOUtils.toByteArray(isFile);
                    try {
                        FileUtils.writeByteArrayToFile(newFile, byteFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        isFile.close();
                    } catch (Exception e) {
                        return ok(loadCCSV.render("fail", "Could not save uploaded file."));
                    }
                } catch (Exception e) {
                    return ok(loadCCSV.render("fail", "Could not process uploaded file."));
                }
            } catch (FileNotFoundException e1) {
                return ok(loadCCSV.render("fail", "Could not find uploaded file"));
            }
            return ok(loadCCSV.render("loaded", "File uploaded successfully."));
        } else {
            return ok(loadCCSV.render("fail", "Error uploading file. Please try again."));
        }
    }
}
