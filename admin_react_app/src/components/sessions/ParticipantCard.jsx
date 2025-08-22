import React, { useState } from 'react';
import { Button, ConfirmationModal } from '../common';

const ParticipantCard = ({ participant, onCheckOut }) => {
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [isCheckingOut, setIsCheckingOut] = useState(false);

  // Get initials for avatar
  const getInitials = (name) => {
    return name
      .split(' ')
      .map(word => word[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  // Handle check out confirmation
  const handleCheckOut = async () => {
    try {
      setIsCheckingOut(true);
      await onCheckOut(participant.ticket_id);
      setShowConfirmModal(false);
    } catch (error) {
      console.error('Check out failed:', error);
    } finally {
      setIsCheckingOut(false);
    }
  };

  return (
    <>
      <div className="flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
        {/* Participant Info */}
        <div className="flex items-center space-x-3">
          {/* Avatar */}
          <div 
            className="w-10 h-10 rounded-full flex items-center justify-center text-white font-medium text-sm"
            style={{ backgroundColor: participant.avatar_color }}
          >
            {getInitials(participant.participant_name)}
          </div>
          
          {/* Details */}
          <div>
            <h4 className="font-medium text-gray-900">{participant.participant_name}</h4>
            <p className="text-sm text-gray-600">
              Vehicle: {participant.vehicle_reg_no} • Lot {participant.parkinglot_id} • {participant.duration}
            </p>
          </div>
        </div>

        {/* Actions */}
        <div className="flex items-center space-x-2">
          {/* Warning icon (optional - for long sessions) */}
          {participant.duration.includes('3h') && (
            <div className="w-6 h-6 bg-yellow-100 rounded-full flex items-center justify-center">
              <svg className="w-4 h-4 text-yellow-600" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
              </svg>
            </div>
          )}
          
          {/* Check Out Button */}
          <Button
            variant="danger"
            size="sm"
            onClick={() => setShowConfirmModal(true)}
            className="!p-2"
            title="Check Out Vehicle"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </Button>
        </div>
      </div>

      {/* Confirmation Modal */}
      <ConfirmationModal
        isOpen={showConfirmModal}
        onClose={() => setShowConfirmModal(false)}
        onConfirm={handleCheckOut}
        title="Check Out Vehicle"
        message={`Are you sure you want to check out ${participant.participant_name}'s vehicle (${participant.vehicle_reg_no})?`}
        confirmText="Check Out"
        cancelText="Cancel"
        isLoading={isCheckingOut}
        variant="danger"
      />
    </>
  );
};

export default ParticipantCard;