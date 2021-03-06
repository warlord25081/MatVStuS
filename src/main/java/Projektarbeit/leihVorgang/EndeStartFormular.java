package Projektarbeit.leihVorgang;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CamundaProjekt.leihVorgangStuS.Datenbankzugang;

public class EndeStartFormular implements JavaDelegate {

	private static final Logger L =  (Logger) LoggerFactory.getLogger(EndeStartFormular.class);
	@Override
	public void execute(DelegateExecution execute) throws Exception {
		// TODO Auto-generated method stub
		String matBez = (String)execute.getVariable("matBez");
		L.info("Ausgewähltes Material: " + matBez);
		Connection conn = null;
		try {
			L.info("* Treiber laden");
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception e) {
			L.error("Unable to load driver.");
			e.printStackTrace();
		}
		try {
			L.info("* Verbindung aufbauen");
			String url = "jdbc:mysql://" + Datenbankzugang.hostname + ":" + Datenbankzugang.port + "/" + Datenbankzugang.dbname;
			conn = DriverManager.getConnection(url, Datenbankzugang.user, Datenbankzugang.password);
			
			
			
		} catch (SQLException sqle) {
			L.error("SQLException: " + sqle.getMessage() + "/n SQLState: " + sqle.getSQLState() + " VendorError: " + sqle.getErrorCode());

		}
		L.info("Start Auslesen des Materials");
		String sql = "select * from MaterialArt where beschreibung = ?";
		try(PreparedStatement ps = conn.prepareStatement(sql)){
			
			ps.setString(1, matBez);
			L.info("SQL Anfrage: " + ps.toString());
			try(ResultSet rs = ps.executeQuery()){
				if(rs.next())
				{
					int matArtID = rs.getInt("idMatArt");
					L.info("MaterialArtID: " + matArtID);
					execute.setVariable("matArtID", matArtID);
				}
				else
				{
					L.warn("Es gibt keine MaterialArt zu dem Material mit der Bezeichnung:" + matBez);
				}
				
			}catch  (SQLException e) {
			L.error(""+e);
			//throw new Exception(e);
			}
		L.info("Ende des Einlesens");
		} catch (SQLException e) {	
			e.printStackTrace();
		}
	}

}
