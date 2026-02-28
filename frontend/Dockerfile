# Use Node.js for building and running Angular
FROM node:20

# Set working directory inside container
WORKDIR /app

# Copy package.json and install dependencies first
COPY package*.json ./
RUN npm install

# Copy the rest of the source code
COPY . .    

# Expose Angular default port
EXPOSE 4200

# Run Angular in dev mode
CMD ["npm", "run","start"]
