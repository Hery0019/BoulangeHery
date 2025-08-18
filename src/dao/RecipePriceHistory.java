package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Locale;

public class RecipePriceHistory {

    private int id;
    private int idRecipe = 1; // Nouvel attribut
    private double priceBefore = 0.0;
    private double priceAfter = 0.0;
    private LocalDate changeDate = LocalDate.now();

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter humanDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy",
            Locale.FRENCH);

    public RecipePriceHistory() {
    }

    public RecipePriceHistory(int id) {
        this.id = id;
    }


    public RecipePriceHistory(int id, int idRecipe, double priceBefore, double priceAfter, 
            LocalDate changeDate) {
        this.id = id;
        this.idRecipe = idRecipe;
        this.priceBefore = priceBefore;
        this.priceAfter = priceAfter;
        this.changeDate = changeDate;
    }

    public RecipePriceHistory(int idRecipe, double priceBefore, double priceAfter,
            LocalDate changeDate) {
        this.idRecipe = idRecipe;
        this.priceBefore = priceBefore;
        this.priceAfter = priceAfter;
        this.changeDate = changeDate;
    }



    public static ArrayList<RecipePriceHistory> all() throws Exception {
        ArrayList<RecipePriceHistory> recipePriceHistories = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getPostgesConnection();
            statement = connection.prepareStatement("SELECT * FROM recipe_price_history");
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id_recipe_price_history");
                int idRecipe = resultSet.getInt("id_recipe");
                double priceBefore = resultSet.getDouble("price_before");
                double priceAfter = resultSet.getDouble("price_after");
                LocalDate changeDate = resultSet.getDate("change_date").toLocalDate();
        
                recipePriceHistories.add(
                        new RecipePriceHistory(id, idRecipe, priceBefore, priceAfter, changeDate));
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

        return recipePriceHistories;
    }

    public static ArrayList<RecipePriceHistory> search(
            int searchidRecipe,
            LocalDate minRecipePriceHistoryDate,
            LocalDate maxRecipePriceHistoryDate,
            double minPriceBefore,
            double maxPriceBefore,
            double minPriceAfter,
            double maxPriceAfter) throws Exception {

        ArrayList<RecipePriceHistory> recipePriceHistories = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getPostgesConnection();

            StringBuilder sql = new StringBuilder(
                    "SELECT * FROM recipe_price_history" +
                    " WHERE 1=1");


            if (searchidRecipe != 0) {
                sql.append(" AND id_recipe = ?");
            }
            if (minRecipePriceHistoryDate != null) {
                sql.append(" AND change_date >= ?");
            }
            if (maxRecipePriceHistoryDate != null) {
                sql.append(" AND change_date <= ?");
            }
            if (minPriceBefore != 0.0) {
                sql.append(" AND price_before >= ?");
            }
            if (maxPriceBefore != 0.0) {
                sql.append(" AND price_before <= ?");
            }
            if (minPriceAfter != 0.0) {
                sql.append(" AND price_after >= ?");
            }
            if (maxPriceAfter != 0.0) {
                sql.append(" AND price_after <= ?");
            }


            sql.append(" ORDER BY id_recipe_price_history ASC");
            statement = connection.prepareStatement(sql.toString());

            int paramIndex = 1;

            if (searchidRecipe != 0) {
                statement.setInt(paramIndex++, searchidRecipe);
            }
            if (minRecipePriceHistoryDate != null) {
                statement.setDate(paramIndex++, Date.valueOf(minRecipePriceHistoryDate));
            }
            if (maxRecipePriceHistoryDate != null) {
                statement.setDate(paramIndex++, Date.valueOf(maxRecipePriceHistoryDate));
            }
            if (minPriceBefore != 0.0) {
                statement.setDouble(paramIndex++, minPriceBefore);
            }
            if (maxPriceBefore != 0.0) {
                statement.setDouble(paramIndex++, maxPriceBefore);
            }
            if (minPriceAfter != 0.0) {
                statement.setDouble(paramIndex++, minPriceAfter);
            }
            if (maxPriceAfter != 0.0) {
                statement.setDouble(paramIndex++, maxPriceAfter);
            }
          
          

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id_recipe_price_history");
                int idRecipe = resultSet.getInt("id_recipe");
                LocalDate changeDate = resultSet.getDate("change_date").toLocalDate();
                double priceBefore = resultSet.getDouble("price_before");
                double priceAfter = resultSet.getDouble("price_after");

                recipePriceHistories.add(new RecipePriceHistory(id, idRecipe, priceBefore, priceAfter, changeDate));
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (resultSet != null)
                resultSet.close();
            if (statement != null)
                statement.close();
            if (connection != null)
                connection.close();
        }

        return recipePriceHistories;
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

    public double getPriceBefore() {
        return this.priceBefore;
    }

    public void setPriceBefore(double priceBefore) {
        this.priceBefore = priceBefore;
    }

    public double getPriceAfter() {
        return this.priceAfter;
    }

    public void setPriceAfter(double priceAfter) {
        this.priceAfter = priceAfter;
    }

    public void setIdRecipe(int idRecipe) {
        this.idRecipe = idRecipe;
    }


    public LocalDate getChangeDate() {
        return changeDate;
    }

    public String getFormattedChangeDate() {
        return changeDate.format(dateFormatter);
    }

    public String getHumanFormattedChangeDate() {
        return changeDate.format(humanDateFormatter);
    }

    public void setChangeDate(LocalDate changeDate) {
        this.changeDate = changeDate;
    }
}
