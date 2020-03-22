'use strict';

var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var dateField = document.querySelector('#date');
var anomalyArea = document.querySelector('#anomalyArea');

var stompClient = null;

var itemSymbolsInUse = [];
var actualData = {};
var historicPrediction = {};
const itemTemplate = '<div id="{symbol}" class="currency_container">'+
                     '<div class="left">'+
                     '  <div class="movement down" id="{symbol}-movement"></div>'+
                     '  <div class="left_inner">'+
                     '      <span id="{symbol}-symbol" class="symbol">{symbol}</span>'+
                     '      <span id="{symbol}-label" class="label">{label}</span>'+
                     '      <span id="{symbol}-price" class="price">{close}</span>'+
                     '  </div>'+
                     '</div>'+
                     '<div class="right">'+
                     '	<div class="category">'+
                     '		<span class="category_title">High</span>'+
                     '		<span class="category_number" id="{symbol}-high">{high}</span>'+
                     '	</div>'+
                     '    <div class="category">'+
                     '		<span class="category_title">Low</span>'+
                     '		<span class="category_number" id="{symbol}-low">{low}</span>'+
                     '	</div>'+
                     '	<div class="category">'+
                     '		<span class="category_title">Spread</span>'+
                     '		<span class="category_number" id="{symbol}-spread">{spread}</span>'+
                     '	</div>'+
                     '    <div class="category">'+
                     '		<span class="category_title">Open</span>'+
                     '		<span class="category_number" id="{symbol}-open">{open}</span>'+
                     '	</div>'+
                     '    <div class="category">'+
                     '		<span class="category_title">Close</span>'+
                     '		<span class="category_number" id="{symbol}-close">{close}</span>'+
                     '	</div>'+
                     '    <div class="category">'+
                     '		<span class="category_title">Volume</span>'+
                     '		<span class="category_number" id="{symbol}-volume">{volume}</span>'+
                     '	</div>'+
                     '</div>'+
                     '<div class="graph">'+
                     '  <div id="{symbol}-graph"></div>'+
                     '</div>'+
                     '</div>';

const anomalyTemplate = '<div class="anomaly_container"><span><b>{currency}</b> <i>({date})</i></span><span style="text-align: right">abnormal {anomalyType} found<br /><small>{is}, expected {low} - {up}</small></span></div>';

const fmtr = new Intl.NumberFormat('us-us', {
                 style: 'decimal',
                 useGrouping: false,
                 minimumFractionDigits: 2,
                 maximumFractionDigits: 4
               });

const baseUrLForPrognosis = "http://localhost:8080/ml/prognosis/"

var margin = {top: 10, right: 10, bottom: 20, left: 30},
    width = 450 - margin.right,
    height = 200 - margin.bottom;

var dateTimeStamp = null;

function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function format(num) {
  return fmtr.format(num);
}

function triggerStream() {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "http://localhost:8080/stream/trigger", true);
    xhttp.send();
    dateField.innerText = "awaiting simulation begin...";
}

function redraw(symbol) {
    d3.csv(baseUrLForPrognosis + symbol).then(function(data) {
        if (data !== null && data.length > 1) {
            data.forEach(function(d) {
                d.low = +d.low;
                d.high = +d.high;
                d.date = new Date(parseInt(d.date));
              });

            historicPrediction[symbol] = historicPrediction[symbol].concat(data)
            if (document.getElementById('historyOption').checked) {
                data = historicPrediction[symbol];
            }

            var svg = d3.select("#" + symbol + "-svg");


            var xRangeMin = margin.left;
            var xRangeMax = width;

            var x = d3.scaleTime()
                .domain(d3.extent(data, function(d) { return d.date; }))
                .range([ xRangeMin, xRangeMax ]);

            var yMin = d3.min(data, function(d) { return d.low * 0.95 });
            var yMax = d3.max(data, function(d) { return d.high / 0.95 });

            yMin = Math.min(yMin, d3.min(actualData[symbol], function(d) {
                    if (x(d.date) >= xRangeMin && x(d.date) <= xRangeMax) {
                        return d.y * 0.9
                    }
                    return 100000000;
                }
            ));

            yMax = Math.max(yMax, d3.max(actualData[symbol], function(d) {
                    if (x(d.date) >= xRangeMin && x(d.date) <= xRangeMax) {
                        return d.y / 0.9
                    }
                    return 0;
                }
            ));

            // Add Y axis
            var y = d3.scaleLinear()
              .domain([yMin, yMax])
              .range([ height, margin.top ]);

            svg.selectAll("*").remove();

            svg.append("g")
                .attr("transform", "translate(0," + height + ")")
                .attr("class", "lightAxis")
                .call(d3.axisBottom(x)
                        //.ticks(d3.timeDay.every(3))
                        .ticks(3)
                        .tickFormat(d3.timeFormat('%d.%m'))
                      );

            svg.append("g")
                .attr("transform", "translate("+margin.left+",0)")
                .attr("class", "lightAxis")
                .call(d3.axisLeft(y).ticks(3));

            // Show prediction interval
            svg.append("path")
              .datum(data)
              .attr("fill", "#cce5df")
              .attr("stroke", "none")
              .attr("d", d3.area()
                .x(function(d) { return x(d.date) })
                .y0(function(d) { return y(d.low) })
                .y1(function(d) { return y(d.high) })
                );

            // Add the line
            svg
              .append("path")
              .datum(actualData[symbol])
              .attr("fill", "none")
              .attr("stroke", "steelblue")
              .attr("stroke-width", 1.5)
              .attr("d", d3.line()
                .x(function(d) { return x(d.date) })
                .y(function(d) { return y(d.y) })
                );
        }



    })

}

function pauseSimulation() {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "http://localhost:8080/stream/pauseSimulation", true);
    xhttp.send();
}
function continueSimulation() {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "http://localhost:8080/stream/resumeSimulation", true);
    xhttp.send();
}

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, function() {});
}


function onConnected() {
    // Subscribe to the topics
    stompClient.subscribe('/topic/currencyRaw', handleCurrencyInfo);
    stompClient.subscribe('/topic/currentDate', handleDate);
    stompClient.subscribe('/topic/anomaly', handleAnomaly);
}

function handleAnomaly(payload) {
    var anomaly = JSON.parse(payload.body);
    anomalyArea.innerHTML = anomalyTemplate.replace("{currency}", anomaly.currency)
                                             .replace("{date}", anomaly.date)
                                             .replace("{anomalyType}", anomaly.anomalyType)
                                             .replace("{is}", anomaly.isValue)
                                             .replace("{low}", anomaly.low)
                                             .replace("{up}", anomaly.up)
                                             + anomalyArea.innerHTML;
}

function handleDate(payload) {
    dateField.innerText = "Simulated date: " + moment(payload.body.slice(1, -1)).format('LLL');
    dateTimeStamp = moment(payload.body.slice(1, -1));
}

function handleCurrencyInfo(payload) {
    var currency = JSON.parse(payload.body);

    if (itemSymbolsInUse.includes(currency.symbol)) {
        document.querySelector('#'+currency.symbol+'-symbol').innerText     = currency.symbol;
        document.querySelector('#'+currency.symbol+'-label').innerText      = currency.symbolName;
        document.querySelector('#'+currency.symbol+'-price').innerText      = format(currency.close);

        document.querySelector('#'+currency.symbol+'-high').innerText       = currency.high.toFixed(2);
        document.querySelector('#'+currency.symbol+'-low').innerText        = currency.low.toFixed(2);
        document.querySelector('#'+currency.symbol+'-volume').innerText     = numberWithCommas(currency.volume);
        document.querySelector('#'+currency.symbol+'-open').innerText       = currency.open.toFixed(2);
        document.querySelector('#'+currency.symbol+'-close').innerText      = currency.close.toFixed(2);
        document.querySelector('#'+currency.symbol+'-spread').innerText     = currency.spread;

        document.querySelector('#'+currency.symbol+'-movement').className = "movement " + (currency.open >= currency.close ? "down" : "up");
        if (!(currency.symbol in actualData)) {
            actualData[currency.symbol] = [];
            historicPrediction[currency.symbol] = [];
        }
        actualData[currency.symbol].push({
            date: moment(currency.date, "DD-MM-YYYY").toDate(),
            y: (parseFloat(currency.low) + parseFloat(currency.high)) / 2
        });
        redraw(currency.symbol);
    }
    else {
        let htmlCode = itemTemplate.replace(/\{symbol\}/g, currency.symbol)
                                    .replace("{label}", currency.symbolName)
                                    .replace("{close}", currency.close.toFixed(2))
                                    .replace("{open}", currency.open.toFixed(2))
                                    .replace("{high}", currency.high.toFixed(2))
                                    .replace("{low}", currency.low.toFixed(2))
                                    .replace("{volume}", numberWithCommas(currency.volume))
                                    .replace("{spread}", currency.spread);


        messageArea.innerHTML += htmlCode;
        document.querySelector('#'+currency.symbol+'-movement').className = "movement " + (currency.open >= currency.close ? "down" : "up");
        itemSymbolsInUse.push(currency.symbol);
        messageArea.scrollTop = messageArea.scrollHeight;

        d3.select("#"+currency.symbol+"-graph")
          .append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .attr("id", currency.symbol+"-svg")
          .append("g")
            .attr("transform",
                  "translate(" + margin.left + "," + margin.top + ")");
        document.querySelector("#" + currency.symbol+"-svg").innerHTML += "<text x='20' y='100' fill='black'>waiting for a prediction model...</text>";
    }
}

connect();
