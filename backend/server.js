const { StreamChat } = require("stream-chat");
require("dotenv").config();

const apiKey = process.env.STREAM_API_KEY;
const apiSecret = process.env.STREAM_API_SECRET;

const serverClient = StreamChat.getInstance(apiKey, apiSecret);
const express = require("express");
const app = express();
const port = 3000;

app.use(express.json());

app.post("/getUserToken", (req, res) => {
  const { userId } = req.body;

  console.log("userId", userId);

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

app.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
