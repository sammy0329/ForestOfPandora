const express = require("express");
const http = require("http");
const WebSocket = require("ws");
const url = require("url");

// import express from "exress";
// import http from "http";
// import WebSocket from "ws";
// import url from "url";

const app = express();
const server = http.createServer(app);
const wss = new WebSocket.Server({ noServer: true });

const projectGroups = new Map();

server.on("upgrade", (request, socket, head) => {
  const pathname = url.parse(request.url).pathname;
  const projectId = pathname.split("/")[1]; // URL에서 projectId 추출

  wss.handleUpgrade(request, socket, head, (ws) => {
    wss.emit("connection", ws, projectId);
  });
});

wss.on("connection", (ws, projectId) => {
  console.log(`WebSocket 연결 활성화, projectId: ${projectId}`);
  if (!projectGroups.has(projectId)) {
    projectGroups.set(projectId, new Set());
    console.log("여기도 들어오나??");
  }
  const clients = projectGroups.get(projectId);
  console.log(`콘솔찍어라....${clients}`);
  clients.add(ws);

  ws.on("message", (message) => {
    console.log(`message: ${message}`);
    [...clients].forEach((client) => {
      if (client !== ws && client.readyState === WebSocket.OPEN) {
        console.log(`메시지 보내기 : ${message}`);
        client.send(message.toString());
      }
    });
  });

  ws.on("close", () => {
    clients.delete(ws);
    if (clients.size === 0) {
      console.log("서버닫힘");
      projectGroups.delete(projectId);
    }
  });
});

server.listen(8888, () => {
  console.log("서버가 8888번 포트에서 실행 중입니다.");
});
