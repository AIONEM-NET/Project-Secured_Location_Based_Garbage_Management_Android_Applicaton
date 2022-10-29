-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 29, 2022 at 07:26 PM
-- Server version: 10.4.22-MariaDB
-- PHP Version: 7.4.27

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `location-garbage-database`
--

-- --------------------------------------------------------

--
-- Table structure for table `admins`
--

CREATE TABLE `admins` (
  `id` int(10) NOT NULL,
  `emal` varchar(50) NOT NULL,
  `password` varchar(128) NOT NULL,
  `type` varchar(10) NOT NULL,
  `status` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `districts`
--

CREATE TABLE `districts` (
  `district_name` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `drivers`
--

CREATE TABLE `drivers` (
  `id` int(10) NOT NULL,
  `name` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `phone` varchar(10) NOT NULL,
  `password` varchar(128) NOT NULL,
  `district` varchar(20) NOT NULL,
  `status` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `garbage`
--

CREATE TABLE `garbage` (
  `id` int(10) NOT NULL,
  `user_id` int(10) NOT NULL,
  `user_phone` varchar(10) NOT NULL,
  `district` varchar(20) NOT NULL,
  `routeNo` varchar(10) NOT NULL,
  `houseNo` varchar(10) NOT NULL,
  `trash` varchar(50) NOT NULL,
  `price` int(10) NOT NULL,
  `amount` int(10) NOT NULL,
  `time` varchar(30) NOT NULL,
  `status` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `trashes`
--

CREATE TABLE `trashes` (
  `id` int(10) NOT NULL,
  `trash_name` varchar(50) NOT NULL,
  `trash_type` varchar(10) NOT NULL,
  `price` int(10) NOT NULL,
  `district` varchar(20) NOT NULL,
  `status` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `trash_type`
--

CREATE TABLE `trash_type` (
  `trash_type` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(10) NOT NULL,
  `name` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `phone` varchar(10) NOT NULL,
  `password` varchar(128) NOT NULL,
  `district` varchar(20) NOT NULL,
  `routeNo` varchar(10) NOT NULL,
  `houseNo` varchar(10) NOT NULL,
  `status` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admins`
--
ALTER TABLE `admins`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `districts`
--
ALTER TABLE `districts`
  ADD UNIQUE KEY `district_name` (`district_name`);

--
-- Indexes for table `drivers`
--
ALTER TABLE `drivers`
  ADD PRIMARY KEY (`id`),
  ADD KEY `district` (`district`);

--
-- Indexes for table `garbage`
--
ALTER TABLE `garbage`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `user_phone` (`user_phone`),
  ADD KEY `district` (`district`),
  ADD KEY `routeNo` (`routeNo`),
  ADD KEY `trash` (`trash`),
  ADD KEY `price` (`price`),
  ADD KEY `garbage_houseNo` (`houseNo`);

--
-- Indexes for table `trashes`
--
ALTER TABLE `trashes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `district` (`district`),
  ADD KEY `trash_type` (`trash_type`),
  ADD KEY `trash_name` (`trash_name`),
  ADD KEY `price` (`price`);

--
-- Indexes for table `trash_type`
--
ALTER TABLE `trash_type`
  ADD UNIQUE KEY `trash_type` (`trash_type`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD KEY `district` (`district`),
  ADD KEY `phone` (`phone`),
  ADD KEY `name` (`name`),
  ADD KEY `routeNo` (`routeNo`),
  ADD KEY `houseNo` (`houseNo`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admins`
--
ALTER TABLE `admins`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `drivers`
--
ALTER TABLE `drivers`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `garbage`
--
ALTER TABLE `garbage`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `trashes`
--
ALTER TABLE `trashes`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `drivers`
--
ALTER TABLE `drivers`
  ADD CONSTRAINT `drivers_district` FOREIGN KEY (`district`) REFERENCES `districts` (`district_name`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `garbage`
--
ALTER TABLE `garbage`
  ADD CONSTRAINT `garbage_district` FOREIGN KEY (`district`) REFERENCES `trash_type` (`trash_type`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `garbage_houseNo` FOREIGN KEY (`houseNo`) REFERENCES `users` (`houseNo`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `garbage_price` FOREIGN KEY (`price`) REFERENCES `trashes` (`price`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `garbage_routeNo` FOREIGN KEY (`routeNo`) REFERENCES `users` (`routeNo`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `garbage_trash` FOREIGN KEY (`trash`) REFERENCES `trashes` (`trash_name`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `garbage_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `garbage_user_phone` FOREIGN KEY (`user_phone`) REFERENCES `users` (`phone`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `trashes`
--
ALTER TABLE `trashes`
  ADD CONSTRAINT `trashes_district` FOREIGN KEY (`district`) REFERENCES `districts` (`district_name`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `trashes_type` FOREIGN KEY (`trash_type`) REFERENCES `trash_type` (`trash_type`);

--
-- Constraints for table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_district` FOREIGN KEY (`district`) REFERENCES `districts` (`district_name`) ON DELETE NO ACTION ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
