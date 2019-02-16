const path = require('path');

module.exports = {
  entry: [
    "./ui/app.tsx",
    "./ui/app.scss"
  ],
    output: {
        filename: "bundle.js",
        path: path.join(__dirname, "public")
    },

    // Enable sourcemaps for debugging webpack's output.
    devtool: "source-map",

    resolve: {
        // Add '.ts' and '.tsx' as resolvable extensions.
        extensions: [".ts", ".tsx", ".js", ".json"]
    },

    module: {
        rules: [
            // All files with a '.ts' or '.tsx' extension will be handled by 'awesome-typescript-loader'.
            { test: /\.tsx?$/, loader: "awesome-typescript-loader" },
            { test: /\.scss$/, use: ["file-loader?name=[name].css", "extract-loader", "css-loader", { loader: "sass-loader", options:{ implementation: require("sass")}}] },

            // All output '.js' files will have any sourcemaps re-processed by 'source-map-loader'.
            { enforce: "pre", test: /\.js$/, loader: "source-map-loader" }
        ]
    },
};
