package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Locale;

public class Step {

    private int id;
    private int idRecipe = 1;
    private int number = 1;
    private String instruction = "";
    private LocalTime cookTime = LocalTime.of(0, 0, 0);

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter humanTimeFormatter = new DateTimeFormatterBuilder()
            .appendPattern("H")
            .appendLiteral(" heure ")
            .optionalStart()
            .appendPattern("m")
            .appendLiteral(" minute")
            .optionalEnd()
            .toFormatter(Locale.FRENCH);

    public Step() {
    }

    public Step(int id) {
        this.id = id;
    }

    public Step(int idRecipe, int number, String instruction) {
        this.idRecipe = idRecipe;
        this.number = number;
        this.instruction = instruction;
    }

    public Step(int id, int idRecipe, int number, String instruction) {
        this.id = id;
        this.idRecipe = idRecipe;
        this.number = number;
        this.instruction = instruction;
    }

    public Step(int id, int idRecipe, int number, String instruction, LocalTime cookTime) {
        this.id = id;
        this.idRecipe = idRecipe;
        this.number = number;
        this.instruction = instruction;
        this.cookTime = cookTime;
    }

    public static ArrayList<Step> all() throws Exception {
        ArrayList<Step> steps = new ArrayList<Step>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getPostgesConnection();
            statement = connection.prepareStatement(
                    "SELECT * FROM step");
            resultSet = statement.executeQuery();

            int id;
            int idRecipe;
            int number;
            String instruction;
            LocalTime cookTime;
            while (resultSet.next()) {
                id = resultSet.getInt("id_step");
                idRecipe = resultSet.getInt("id_recipe");
                number = resultSet.getInt("step_number");
                instruction = resultSet.getString("instruction");
                cookTime = resultSet.getTime("cook_time").toLocalTime();

                steps.add(
                        new Step(id, idRecipe, number, instruction, cookTime));
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return steps;
    }

    public static ArrayList<Step> search(
            int searchIdRecipe,
            int minStepNumber,
            int maxStepNumber,
            LocalTime minCookTime,
            LocalTime maxCookTime,
            String searchInstruction) throws Exception {
        ArrayList<Step> steps = new ArrayList<Step>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getPostgesConnection();

            // Start building the SQL query
            StringBuilder sql = new StringBuilder("SELECT * FROM step");
            sql.append(" WHERE instruction ILIKE ?");

            // Add conditions if they are not null
            if (searchIdRecipe != 0) {
                sql.append(" AND id_recipe = ?");
            }
            if (minStepNumber != 0) {
                sql.append(" AND step_number >= ?");
            }
            if (maxStepNumber != 0) {
                sql.append(" AND step_number <= ?");
            }
            if (minCookTime != null) {
                sql.append(" AND cook_time >= ?");
            }
            if (maxCookTime != null) {
                sql.append(" AND cook_time <= ?");
            }
            sql.append(" ORDER BY step_number");

            statement = connection.prepareStatement(
                    sql.toString());

            // Set the search parameters
            int paramIndex = 1;
            statement.setString(paramIndex, "%" + searchInstruction + "%");
            paramIndex++;
            if (searchIdRecipe != 0) {
                statement.setInt(paramIndex, searchIdRecipe);
                paramIndex++;
            }
            if (minStepNumber != 0) {
                statement.setInt(paramIndex, minStepNumber);
                paramIndex++;
            }
            if (maxStepNumber != 0) {
                statement.setInt(paramIndex, maxStepNumber);
                paramIndex++;
            }
            if (minCookTime != null) {
                statement.setTime(paramIndex++, Time.valueOf(minCookTime));
            }
            if (maxCookTime != null) {
                statement.setTime(paramIndex++, Time.valueOf(maxCookTime));
            }

            resultSet = statement.executeQuery();

            int id;
            int idRecipe;
            int number;
            String instruction;
            LocalTime cookTime;
            while (resultSet.next()) {
                id = resultSet.getInt("id_step");
                idRecipe = resultSet.getInt("id_recipe");
                number = resultSet.getInt("step_number");
                instruction = resultSet.getString("instruction");
                cookTime = resultSet.getTime("cook_time").toLocalTime();

                steps.add(
                        new Step(id, idRecipe, number, instruction, cookTime));
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return steps;
    }

    public void find() throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getPostgesConnection();
            statement = connection.prepareStatement(
                    "SELECT * FROM step"
                            + " WHERE id_step = ?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                idRecipe = resultSet.getInt("id_recipe");
                number = resultSet.getInt("step_number");
                instruction = resultSet.getString("instruction");
                cookTime = resultSet.getTime("cook_time").toLocalTime();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public void create() throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBConnection.getPostgesConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(
                    "INSERT INTO step(id_recipe, step_number, instruction, cook_time)"
                            + " VALUES (?, ?, ?, ?)");
            statement.setInt(1, idRecipe);
            statement.setInt(2, number);
            statement.setString(3, instruction);
            statement.setTime(4, Time.valueOf(cookTime));
            statement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            if (connection != null)
                connection.rollback();
            throw e;
        } finally {
            if (statement != null)
                statement.close();
            if (connection != null)
                connection.close();
        }
    }

    public void update() throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBConnection.getPostgesConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(
                    "UPDATE step"
                            + " SET id_recipe = ?, step_number = ?, instruction = ?, cook_time = ?"
                            + " WHERE id_step = ?");
            statement.setInt(1, idRecipe);
            statement.setInt(2, number);
            statement.setString(3, instruction);
            statement.setTime(4, Time.valueOf(cookTime));
            statement.setInt(5, id);
            statement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            if (connection != null)
                connection.rollback();
            throw e;
        } finally {
            if (statement != null)
                statement.close();
            if (connection != null)
                connection.close();
        }
    }

    public void delete() throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBConnection.getPostgesConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(
                    "DELETE FROM step"
                            + " WHERE id_step = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            statement.close();
            connection.close();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdRecipe() {
        return idRecipe;
    }

    public void setIdRecipe(int idRecipe) {
        this.idRecipe = idRecipe;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getInstruction() {
        return instruction;
    }

    public LocalTime getCookTime() {
        return cookTime;
    }

    public String getFormattedCookTime() {
        return cookTime.format(timeFormatter);
    }

    public String getHumanFormattedCookTime() {
        return cookTime.format(humanTimeFormatter);
    }

    public void setCookTime(LocalTime cookTime) {
        this.cookTime = cookTime;
    }

    public String getInstructionExcerpt() {
        return instruction.length() < 21 ? instruction : instruction.substring(0, 21) + "...";
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    @Override
    public String toString() {
        return "Step [id=" + id + ", idRecipe=" + idRecipe + ", number=" + number + ", instruction=" + instruction
                + "]";
    }

}
