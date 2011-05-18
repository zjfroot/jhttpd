package jifeng.jhttpd.poc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * Date: 5/18/11
 * Time: 4:38 PM
 *
 * @author Jifeng Zhang
 */
public class ConfFile extends Properties {

    private static ConfFile conf;

    private ConfFile(){

    }

    public static ConfFile getInstance(){
        if(conf == null){
            conf = new ConfFile();
            try {
                InputStream in =
                        ConfFile.class.getResourceAsStream("/jhttpd.conf");
                if(in != null){
                    conf.load(in);
                }else{

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return conf;
        }else{
            return conf;
        }

    }
}
