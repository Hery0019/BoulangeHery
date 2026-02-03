<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="util.Html" %>
<%@ page import="util.Money" %>
<%@ page import="dao.RecipeSell" %>
<% RecipeSell sell = (RecipeSell) request.getAttribute("sell"); %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ticket de vente n° <%= Html.esc(sell.getId()) %></title>
    <link rel="icon" type="image/x-icon" href="assets/img/favicon/favicon.png"/>
    <style>
        /* Page autonome : un ticket n'a ni menu ni barre de navigation. */
        body { font-family: system-ui, "Segoe UI", sans-serif; background: #f5f5f9; margin: 0; padding: 2rem; color: #32384e; }
        .ticket { max-width: 22rem; margin: 0 auto; background: #fff; padding: 1.5rem; border-radius: .5rem;
                  box-shadow: 0 .1rem .5rem rgba(0,0,0,.1); }
        .ticket h1 { font-size: 1.25rem; margin: 0 0 .25rem; text-align: center; }
        .ticket .muted { color: #8592a3; font-size: .8rem; text-align: center; margin: 0 0 1rem; }
        table { width: 100%; border-collapse: collapse; font-size: .9rem; }
        td { padding: .3rem 0; vertical-align: top; }
        td.value { text-align: right; }
        .line { border-top: 1px dashed #d9dee3; }
        .total td { font-weight: 700; font-size: 1rem; padding-top: .6rem; }
        .actions { max-width: 22rem; margin: 1rem auto 0; display: flex; gap: .5rem; justify-content: center; }
        .actions a, .actions button { font: inherit; padding: .4rem .9rem; border-radius: .375rem; border: 1px solid #d9dee3;
                                      background: #fff; color: #32384e; text-decoration: none; cursor: pointer; }
        .actions .primary { background: #ffab00; border-color: #ffab00; color: #fff; }
        @media print {
            /* À l'impression : pas de fond, pas de boutons, juste le ticket. */
            body { background: #fff; padding: 0; }
            .ticket { box-shadow: none; max-width: none; }
            .actions { display: none; }
        }
    </style>
</head>
<body>
<div class="ticket">
    <h1>BoulangeHery</h1>
    <p class="muted">Ticket n° <%= Html.esc(sell.getId()) %> — <%= Html.esc(sell.getHumanFormattedCreatedDate()) %></p>

    <table>
        <tr><td>Client</td><td class="value"><%= Html.esc(sell.getClientLabel()) %></td></tr>
        <tr><td>Vendeur</td><td class="value"><%= Html.esc(sell.getVendeurName()) %></td></tr>
        <tr class="line"><td colspan="2"></td></tr>
        <tr>
            <td><%= Html.esc(sell.getRecipeTitle()) %><br>
                <span class="muted"><%= Html.esc(sell.getCombien()) %> x <%= Html.esc(Money.format(sell.getUnitPrice())) %></span>
            </td>
            <td class="value"><%= Html.esc(Money.format(sell.getTotal())) %></td>
        </tr>
        <tr class="line total"><td>Total</td><td class="value"><%= Html.esc(Money.format(sell.getTotal())) %></td></tr>
        <tr><td>Reçu</td><td class="value"><%= Html.esc(Money.format(sell.getArgent())) %></td></tr>
        <tr><td>Rendu</td><td class="value"><%= Html.esc(Money.format(sell.getReste())) %></td></tr>
    </table>

    <p class="muted" style="margin-top: 1.5rem;">Merci de votre visite !</p>
</div>

<div class="actions">
    <button type="button" class="primary" onclick="window.print()">Imprimer</button>
    <a href="recipe-sell">Retour aux ventes</a>
</div>
</body>
</html>
