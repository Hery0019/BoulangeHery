/**
 * Graphiques du tableau de bord.
 *
 * Les séries sont déposées par la page dans des attributs data-, échappés côté
 * serveur : aucune donnée n'est injectée dans du code JavaScript.
 */
(function () {
  'use strict';

  var PALETTE = ['#ffab00', '#696cff', '#71dd37', '#ff3e1d', '#03c3ec', '#8592a3'];

  function read(id) {
    var el = document.getElementById(id);
    if (!el) return null;
    try {
      return {
        el: el,
        labels: JSON.parse(el.dataset.labels || '[]'),
        values: JSON.parse(el.dataset.values || '[]')
      };
    } catch (e) {
      return null;
    }
  }

  function empty(el) {
    el.innerHTML = '<p class="text-muted mb-0">Aucune donnée sur la période.</p>';
  }

  function render(id, options) {
    var data = read(id);
    if (!data) return;
    if (!data.values.length || data.values.every(function (v) { return Number(v) === 0; })) {
      empty(data.el);
      return;
    }
    var config = options(data);
    // Animation d'un seul tenant : le dessin point par point d'ApexCharts fait
    // apparaître les barres les unes après les autres, ce qui donne un
    // graphique faux pendant une seconde (et sur une capture d'écran).
    config.chart.animations = { enabled: true, speed: 400, animateGradually: { enabled: false } };
    new ApexCharts(data.el, config).render();
  }

  document.addEventListener('DOMContentLoaded', function () {
    if (typeof ApexCharts === 'undefined') return;

    render('chart-revenue', function (d) {
      return {
        chart: { type: 'area', height: 300, toolbar: { show: false } },
        colors: [PALETTE[0]],
        dataLabels: { enabled: false },
        stroke: { curve: 'smooth', width: 3 },
        series: [{ name: 'Encaissé', data: d.values.map(Number) }],
        xaxis: { categories: d.labels },
        yaxis: { labels: { formatter: function (v) { return Math.round(v).toLocaleString('fr-FR'); } } },
        tooltip: { y: { formatter: function (v) { return v.toLocaleString('fr-FR') + ' Ar'; } } }
      };
    });

    render('chart-category', function (d) {
      return {
        chart: { type: 'donut', height: 300 },
        dataLabels: { enabled: false },
        colors: PALETTE,
        labels: d.labels,
        series: d.values.map(Number),
        legend: { position: 'bottom' },
        tooltip: { y: { formatter: function (v) { return v.toLocaleString('fr-FR') + ' Ar'; } } }
      };
    });

    render('chart-top', function (d) {
      return {
        chart: { type: 'bar', height: 300, toolbar: { show: false } },
        colors: [PALETTE[1]],
        plotOptions: { bar: { horizontal: true, borderRadius: 4 } },
        dataLabels: { enabled: false },
        series: [{ name: 'Unités vendues', data: d.values.map(Number) }],
        xaxis: { categories: d.labels }
      };
    });

    render('chart-commissions', function (d) {
      return {
        chart: { type: 'bar', height: 300, toolbar: { show: false } },
        colors: [PALETTE[2]],
        plotOptions: { bar: { borderRadius: 4, columnWidth: '45%' } },
        dataLabels: { enabled: false },
        series: [{ name: 'Commissions', data: d.values.map(Number) }],
        xaxis: { categories: d.labels },
        tooltip: { y: { formatter: function (v) { return v.toLocaleString('fr-FR') + ' Ar'; } } }
      };
    });

    render('chart-production', function (d) {
      return {
        chart: { type: 'bar', height: 300, toolbar: { show: false } },
        colors: [PALETTE[4]],
        plotOptions: { bar: { borderRadius: 4, columnWidth: '45%' } },
        dataLabels: { enabled: false },
        series: [{ name: 'Unités produites', data: d.values.map(Number) }],
        xaxis: { categories: d.labels }
      };
    });

    render('chart-losses', function (d) {
      var labels = { INVENDU: 'Invendu', CASSE: 'Casse', PERIME: 'Périmé', OFFERT: 'Offert' };
      return {
        chart: { type: 'donut', height: 300 },
        dataLabels: { enabled: false },
        colors: [PALETTE[3], PALETTE[5], PALETTE[0], PALETTE[2]],
        labels: d.labels.map(function (l) { return labels[l] || l; }),
        series: d.values.map(Number),
        legend: { position: 'bottom' }
      };
    });
  });
})();
