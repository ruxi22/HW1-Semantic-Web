import React, { createContext, useState, useEffect } from 'react';

export const UserContext = createContext();

export function UserProvider({ children }) {
  const [selectedUser, setSelectedUser] = useState(null);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await fetch('http://localhost:8091/api/users');
        if (response.ok) {
          const data = await response.json();
          setUsers(data);
          if (data.length > 0) {
            setSelectedUser(data[0]);
          }
        }
      } catch (error) {
        console.error('Error fetching users:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, []);

  const changeUser = (userId) => {
    const user = users.find(u => u.id === userId);
    if (user) {
      setSelectedUser(user);
    }
  };

  return (
    <UserContext.Provider value={{ selectedUser, users, loading, changeUser }}>
      {children}
    </UserContext.Provider>
  );
}

