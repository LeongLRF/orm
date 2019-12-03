package core.inerface;

import config.Configuration;

public interface DbFactory  {

   static IDbConnection getDb(Configuration config){
       return null;
   }
}
