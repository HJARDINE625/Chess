package ui;

import java.io.IOException;

public class ExceptionTransformer {
    //oddly enough this is the best way to turn IO exceptions (that are really not IO exceptions), back into usable exceptions here...
    public void transform(Exception e) throws ReportingException {
        //we only want one onpoint exception per exception
        if(e == null) {
            return;
        }
        if(e.getClass() == null){
            return;
        }
        //the real check...
        if(e.getClass() == IOException.class){
            IOException check = (IOException) e;
            String message = check.getMessage();
            var trace = check.getStackTrace();
            throw new ReportingException(message);
        }
    }

}
