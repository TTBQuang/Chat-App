const { StreamChat } = require("stream-chat");
require("dotenv").config();

const apiKey = process.env.STREAM_API_KEY;
const apiSecret = process.env.STREAM_API_SECRET;

const serverClient = StreamChat.getInstance(apiKey, apiSecret);
const express = require("express");
const bodyParser = require("body-parser");
const cors = require("cors");
const admin = require("./firebaseAdmin");
const app = express();
const port = 3000;

app.use(express.json());
app.use(cors());
app.use(bodyParser.json());

app.post("/getUserToken", (req, res) => {
  const { userId } = req.body;

  if (!userId) {
    return res.status(400).send("userId is required");
  }

  try {
    const userToken = serverClient.createToken(userId);
    res.status(200).send({ userToken });
  } catch (error) {
    console.error("Error creating token:", error);
    res.status(500).send("Internal Server Error");
  }
});

app.post("/sendNotification", async (req, res) => {
  const { token, title, body } = req.body;

  if (!token || !title || !body) {
    return res.status(400).json({ error: "Missing parameters" });
  }

  const message = {
    notification: {
      title: title,
      body: body,
    },
    token: token,
  };

  try {
    const response = await admin.messaging().send(message);
    console.log("Successfully sent message:", response);
    res.status(200).json({ message: "Notification sent successfully" });
  } catch (error) {
    console.error("Error sending notification:", error);
    res.status(500).json({ error: "Failed to send notification" });
  }
});

app.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
