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
const itemTemplate = '<div id="{symbol}" class="currency_container">'+
                     '<div class="left">'+
                     '  <div class="movement down" id="{symbol}-movement"></div>'+
                     '  <div class="left_inner">'+
                     '      <span id="{symbol}-symbol" class="symbol">{symbol}</span>'+
                     '      <span id="{symbol}-label" class="label">{symbol}</span>'+
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
                     '</div>';

const anomalyTemplate = '<div class="anomaly_container">{message}</div>';

const fmtr = new Intl.NumberFormat('us-us', {
                 style: 'decimal',
                 useGrouping: false,
                 minimumFractionDigits: 2,
                 maximumFractionDigits: 4
               });
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
    anomalyArea.innerHTML = anomalyTemplate.replace(payload.body.slice(1, -1)) + anomalyArea.innerHTML;
    anomalyArea.scrollTop = anomalyArea.scrollHeight;
}

function handleDate(payload) {
    dateField.innerText = "Simulated date: " + moment(payload.body.slice(1, -1)).format('LLL');
}

function handleCurrencyInfo(payload) {
    var currency = JSON.parse(payload.body);

    if (itemSymbolsInUse.includes(currency.symbol)) {
        document.querySelector('#'+currency.symbol+'-symbol').innerText     = currency.symbol;
        document.querySelector('#'+currency.symbol+'-label').innerText      = currency.symbol;
        document.querySelector('#'+currency.symbol+'-price').innerText      = format(currency.close);

        document.querySelector('#'+currency.symbol+'-high').innerText       = currency.high.toFixed(2);
        document.querySelector('#'+currency.symbol+'-low').innerText        = currency.low.toFixed(2);
        document.querySelector('#'+currency.symbol+'-volume').innerText     = numberWithCommas(currency.volume);
        document.querySelector('#'+currency.symbol+'-open').innerText       = currency.open.toFixed(2);
        document.querySelector('#'+currency.symbol+'-close').innerText      = currency.close.toFixed(2);
        document.querySelector('#'+currency.symbol+'-spread').innerText     = currency.spread;

        document.querySelector('#'+currency.symbol+'-movement').className = "movement " + (currency.open >= currency.close ? "down" : "up");
    }
    else {
        let htmlCode = itemTemplate.replace(/\{symbol\}/g, currency.symbol)
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
    }
}

connect();
