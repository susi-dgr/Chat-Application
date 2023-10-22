package rmi.common;

import java.io.Serializable;
import java.rmi.Remote;

public interface Server extends DatabaseService, ConnectionService, Remote, Serializable {
}
