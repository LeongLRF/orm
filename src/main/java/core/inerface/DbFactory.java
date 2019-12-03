package core.inerface;

import config.Configuration;
import core.inerface.IDbConnection;

public interface DbFactory  {

   static IDbConnection getDb(Configuration config){
       return null;
   }
}
