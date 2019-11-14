package core.inerface;

import java.util.List;

/**
 * @author Leong
 */
public interface IStatement {

    String getSql();

    List<Object> getParams();

}
