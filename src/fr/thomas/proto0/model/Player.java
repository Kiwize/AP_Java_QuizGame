package fr.thomas.proto0.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import fr.thomas.proto0.controller.GameController;
import fr.thomas.proto0.utils.BCrypt;

public class Player implements IModel {

	private int id;
	private String name;
	private String password;
	private GameController controller;

	public Player(String name, GameController controller) {
		this.name = name;
		this.controller = controller;
	}

	public String getName() {
		return name;
	}

	public boolean authenticate(String name, String password) {
		try {
			Statement st = controller.getDatabaseHelper().getStatement(0);
			ResultSet set = st.executeQuery("SELECT idplayer, name FROM Player WHERE name = '" + name + "';");

			if (!set.next()) {
				System.err.println("Unknown user");
				return false;
			} else {
				set = st.executeQuery("SELECT idplayer, name, password FROM Player WHERE name = '" + name + "';");
				if (set.next()) {
					if (BCrypt.checkpw(password, set.getString("password"))) {
						this.id = set.getInt("idplayer");
						this.password = set.getString("password");
						this.name = set.getString("name");
					} else {
						System.err.println("Wrong password !");
						return false;
					}
				}

				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updatePassword(String newPassword) {
		final String encrypted = BCrypt.hashpw(newPassword, BCrypt.gensalt());
		try {
			Statement st = controller.getDatabaseHelper().getStatement(0);
			st.execute("UPDATE Player SET Player.password = '" + encrypted + "' WHERE Player.idplayer = " + id + ";");

			return true;
		} catch (final SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean insert() {
		try {
			Statement st = controller.getDatabaseHelper().getStatement(0);
			boolean res = st.execute("INSERT INTO Player (name) VALUES ('" + name + "');");
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean save() {
		try {
			Statement st = controller.getDatabaseHelper().getStatement(0);
			boolean res = st.execute("UPDATE Player SET name = '" + name + "' WHERE Player.idplayer=" + id + ";");
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public int getID() {
		return id;
	}
	
	public String getPassword() {
		return password;
	}
}
