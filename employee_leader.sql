-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 21, 2022 at 08:03 AM
-- Server version: 10.4.24-MariaDB
-- PHP Version: 8.1.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `belajar`
--

-- --------------------------------------------------------

--
-- Table structure for table `employee_leader`
--

CREATE TABLE `employee_leader` (
  `id` int(11) NOT NULL,
  `name_leader` varchar(50) NOT NULL,
  `email_leader` varchar(40) NOT NULL,
  `gender_leader` varchar(30) NOT NULL,
  `phone_leader` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `employee_leader`
--

INSERT INTO `employee_leader` (`id`, `name_leader`, `email_leader`, `gender_leader`, `phone_leader`) VALUES
(7, 'santo', 'santo@gmail.com', 'Man', '6297654342'),
(10, 'Denny Prayudi', 'Denny_Prayudi@yahoo.com', 'Man', '6297654342');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `employee_leader`
--
ALTER TABLE `employee_leader`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `employee_leader`
--
ALTER TABLE `employee_leader`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
